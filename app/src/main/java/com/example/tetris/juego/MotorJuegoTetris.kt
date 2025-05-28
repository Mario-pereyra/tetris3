package com.example.tetris.juego

import android.os.Handler
import android.os.Looper
import com.example.tetris.modelo.TetrominoModelo

/**
 * Orquesta la lógica principal del juego de Tetris, coordinando el tablero y la pieza activa.
 * Maneja el bucle del juego, la puntuación, los niveles y el estado general del juego.
 *
 * @param anchoTablero El número de columnas del tablero.
 * @param altoTablero El número de filas del tablero.
 */
class MotorJuegoTetris(
    val anchoTablero: Int = 10,
    val altoTablero: Int = 20
) {

    // --- COMPONENTES DEL JUEGO ---
    private val manejadorTablero: ManejadorTablero = ManejadorTablero(anchoTablero, altoTablero)
    private val controladorPiezas: ControladorPiezas = ControladorPiezas(anchoTablero, manejadorTablero)

    // --- ESTADO DEL JUEGO ---
    var puntuacion: Int = 0
        private set
    var nivel: Int = 1
        private set
    var juegoTerminado: Boolean = false
        private set

    // --- CONFIGURACIÓN DEL JUEGO ---
    private val puntosPorLineaSimple: Int = 10
    private val umbralPuntosParaNivelInicial: Int = 100// Según regla del usuario
    private val decrementoDelayCaidaPorNivel: Long = 200L // Milisegundos
    private val delayCaidaMinimo: Long = 100L           // Milisegundos
    private var delayCaidaActualMs: Long = 800L        // Milisegundos

    // --- BUCLE DEL JUEGO ---
    private val manejadorBucleJuego = Handler(Looper.getMainLooper())
    private var runnableBucleJuego: Runnable? = null

    // --- LISTENERS PARA LA UI ---
    var alActualizarVistaJuego: (() -> Unit)? = null
    var alTerminarJuego: ((puntuacionFinal: Int) -> Unit)? = null
    var alActualizarPuntuacionNivel: ((puntuacion: Int, nivel: Int) -> Unit)? = null

    // --- ACCESO PÚBLICO AL ESTADO PARA LA VISTA ---
    /** Proporciona acceso de solo lectura al tablero para la vista. */
    val celdasTablero: Array<Array<Int?>>
        get() = manejadorTablero.celdas

    /** Proporciona acceso de solo lectura a la pieza actual para la vista. */
    val piezaActualParaVista: TetrominoModelo?
        get() = controladorPiezas.piezaActiva

    init {
        reiniciarJuego()
    }

    /**
     * (Re)inicia el juego a su estado inicial.
     */
    fun reiniciarJuego() {
        detenerBucleJuego()

        manejadorTablero.limpiarTablero()
        controladorPiezas.limpiarPiezaActiva() // Asegura que no haya pieza activa al inicio

        puntuacion = 0
        nivel = 1
        juegoTerminado = false
        delayCaidaActualMs = 800L

        // Intenta generar la primera pieza. Si falla (null), el juego termina inmediatamente.
        if (controladorPiezas.generarNuevaPieza() == null) {
            terminarJuego()
        }

        notificarActualizacionPuntuacionNivel()
        notificarActualizacionVista()

        if (!juegoTerminado) {
            iniciarBucleJuego()
        }
    }

    private fun iniciarBucleJuego() {
        if (juegoTerminado || runnableBucleJuego != null) return // No iniciar si ya está terminado o en ejecución

        runnableBucleJuego = Runnable {
            if (!juegoTerminado) {
                tickDelJuego()
                // Volver a programar solo si el juego no ha terminado durante el tick
                if (!juegoTerminado && runnableBucleJuego != null) {
                    manejadorBucleJuego.postDelayed(runnableBucleJuego!!, delayCaidaActualMs)
                }
            }
        }
        manejadorBucleJuego.postDelayed(runnableBucleJuego!!, delayCaidaActualMs)
    }

    private fun detenerBucleJuego() {
        runnableBucleJuego?.let { manejadorBucleJuego.removeCallbacks(it) }
        runnableBucleJuego = null
    }

    /**
     * Lógica que se ejecuta en cada "tick" del bucle del juego.
     */
    private fun tickDelJuego() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) {
            // Si no hay pieza activa (ej. justo después de un hard drop y antes de generar la nueva),
            // no hacemos nada en este tick específico de caída. La generación de la nueva pieza
            // se maneja después de asentar.
            return
        }

        // Intenta mover la pieza hacia abajo
        val seMovioAbajo = controladorPiezas.moverPiezaAbajo()

        if (!seMovioAbajo) {
            // La pieza no pudo moverse hacia abajo, así que debe asentarse.
            val piezaParaAsentar = controladorPiezas.piezaActiva
            if (piezaParaAsentar != null) {
                // Asentar la pieza en el tablero
                val asentamientoExitoso = manejadorTablero.asentarPieza(piezaParaAsentar)
                if (!asentamientoExitoso) { // Pieza se asentó por encima del tablero
                    terminarJuego()
                    notificarActualizacionVista()
                    return
                }
                controladorPiezas.limpiarPiezaActiva() // Ya no es la pieza activa

                // Eliminar líneas completas y actualizar puntuación
                val lineasEliminadas = manejadorTablero.eliminarLineasCompletas()
                if (lineasEliminadas > 0) {
                    actualizarPuntuacionPorLineas(lineasEliminadas)
                    verificarSubidaDeNivel()
                }

                // Generar la siguiente pieza
                if (controladorPiezas.generarNuevaPieza() == null) {
                    // No se pudo generar una nueva pieza (no hay espacio)
                    terminarJuego()
                }
            }
        }
        notificarActualizacionVista()
    }

    private fun actualizarPuntuacionPorLineas(numLineasEliminadas: Int) {
        if (numLineasEliminadas <= 0) return

        val puntosGanados = when {
            numLineasEliminadas >= 2 -> numLineasEliminadas * numLineasEliminadas * puntosPorLineaSimple
            numLineasEliminadas == 1 -> puntosPorLineaSimple
            else -> 0
        }
        puntuacion += puntosGanados
        notificarActualizacionPuntuacionNivel()
    }

    private fun verificarSubidaDeNivel() {
        // Regla: "Al llegar a 5000 puntos se cambia de nivel".
        // "El subir el nivel, solo resetea el tablero y aumenta la velocidad, el puntaje se mantiene".
        // Interpretaremos esto como que el primer cambio de nivel ocurre al alcanzar los 5000 puntos.
        // Si se desean más niveles, la lógica de umbral necesitaría ser más general.
        var subioDeNivel = false
        if (nivel == 1 && puntuacion >= umbralPuntosParaNivelInicial) {
            nivel++
            subioDeNivel = true
        }
        // Ejemplo para niveles subsiguientes (si se quisiera una regla más general):
        // else if (puntuacion >= (umbralPuntosParaNivelInicial * nivel)) { // umbral simple por nivel
        //    nivel++
        //    subioDeNivel = true
        // }

        if (subioDeNivel) {
            // 1. Resetear el tablero
            manejadorTablero.limpiarTablero()
            controladorPiezas.limpiarPiezaActiva() // Pieza actual se va con el tablero viejo

            // 2. Aumentar la velocidad de caída
            delayCaidaActualMs = maxOf(delayCaidaMinimo, delayCaidaActualMs - decrementoDelayCaidaPorNivel)

            // 3. Detener y reiniciar el bucle para aplicar la nueva velocidad y generar pieza
            detenerBucleJuego()

            if (controladorPiezas.generarNuevaPieza() == null) {
                terminarJuego() // Si no hay espacio en el tablero reseteado (improbable pero seguro)
            }

            notificarActualizacionPuntuacionNivel() // Para actualizar la UI del nivel
            // La vista se actualizará con la nueva pieza.

            if (!juegoTerminado) {
                iniciarBucleJuego()
            }
        }
    }

    private fun terminarJuego() {
        if (juegoTerminado) return // Evitar múltiples llamadas
        juegoTerminado = true
        detenerBucleJuego()
        alTerminarJuego?.invoke(puntuacion)
        // La vista debería mostrar un mensaje de "Game Over"
        notificarActualizacionVista() // Para el estado final
    }


    // --- Notificadores a la UI ---
    private fun notificarActualizacionVista() {
        alActualizarVistaJuego?.invoke()
    }

    private fun notificarActualizacionPuntuacionNivel() {
        alActualizarPuntuacionNivel?.invoke(puntuacion, nivel)
    }

    // --- ACCIONES DEL JUGADOR (Delegadas al ControladorPiezas) ---

    fun moverPiezaIzquierda() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) return
        if (controladorPiezas.moverPiezaIzquierda()) {
            notificarActualizacionVista()
        }
    }

    fun moverPiezaDerecha() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) return
        if (controladorPiezas.moverPiezaDerecha()) {
            notificarActualizacionVista()
        }
    }

    fun rotarPieza() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) return
        if (controladorPiezas.rotarPieza()) {
            notificarActualizacionVista()
        }
    }


    /**
     * Deja caer la pieza actual instantáneamente hasta la posición más baja posible
     * y la asienta.
     */
    fun dejarCaerPiezaRapido() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) return

        detenerBucleJuego()
        controladorPiezas.dejarCaerPiezaRapido() // Mueve la pieza a su posición final de caída

        // Ahora, la lógica que sigue es similar a cuando una pieza no puede bajar más en un tick normal
        val piezaParaAsentar = controladorPiezas.piezaActiva
        if (piezaParaAsentar != null) {
            val asentamientoExitoso = manejadorTablero.asentarPieza(piezaParaAsentar)
            if (!asentamientoExitoso) {
                terminarJuego()
                notificarActualizacionVista()
                return
            }
            controladorPiezas.limpiarPiezaActiva()

            val lineasEliminadas = manejadorTablero.eliminarLineasCompletas()
            if (lineasEliminadas > 0) {
                actualizarPuntuacionPorLineas(lineasEliminadas)
                verificarSubidaDeNivel()
            }

            if (controladorPiezas.generarNuevaPieza() == null) {
                terminarJuego()
            }
        }

        notificarActualizacionVista()
        if (!juegoTerminado) {
            iniciarBucleJuego()
        }
    }
}

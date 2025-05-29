package com.example.tetris.juego

import android.os.Handler
import android.os.Looper
import com.example.tetris.modelo.TetrominoModelo

class MotorJuegoTetris(
    val anchoTablero: Int = 10,
    val altoTablero: Int = 20
) {

    private val manejadorTablero: ManejadorTablero = ManejadorTablero(anchoTablero, altoTablero)
    private val controladorPiezas: ControladorPiezas = ControladorPiezas(anchoTablero, manejadorTablero)

    var puntuacion: Int = 0
        private set
    var nivel: Int = 1
        private set
    var juegoTerminado: Boolean = false
        private set

    private val puntosPorLineaSimple: Int = 10
    private val umbralPuntosParaNivelInicial: Int = 100
    private val decrementoDelayCaidaPorNivel: Long = 200L
    private val delayCaidaMinimo: Long = 100L
    private var delayCaidaActualMs: Long = 800L

    private val manejadorBucleJuego = Handler(Looper.getMainLooper())
    private var runnableBucleJuego: Runnable? = null

    var alActualizarVistaJuego: (() -> Unit)? = null
    var alTerminarJuego: ((puntuacionFinal: Int) -> Unit)? = null
    var alActualizarPuntuacionNivel: ((puntuacion: Int, nivel: Int) -> Unit)? = null

    val celdasTablero: Array<Array<Int?>>
        get() = manejadorTablero.celdas

    val piezaActualParaVista: TetrominoModelo?
        get() = controladorPiezas.piezaActiva

    init {
        reiniciarJuego()
    }

    fun reiniciarJuego() {
        detenerBucleJuego()

        manejadorTablero.limpiarTablero()
        controladorPiezas.limpiarPiezaActiva()

        puntuacion = 0
        nivel = 1
        juegoTerminado = false
        delayCaidaActualMs = 800L

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
        if (juegoTerminado || runnableBucleJuego != null) return

        runnableBucleJuego = Runnable {
            if (!juegoTerminado) {
                tickDelJuego()

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

    private fun tickDelJuego() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) {
            return
        }

        val seMovioAbajo = controladorPiezas.moverPiezaAbajo()

        if (!seMovioAbajo) {
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

        var subioDeNivel = false
        if (nivel == 1 && puntuacion >= umbralPuntosParaNivelInicial) {
            nivel++
            subioDeNivel = true
        }

        else if (puntuacion >= (umbralPuntosParaNivelInicial * nivel)) {
            nivel++
            subioDeNivel = true
         }

        if (subioDeNivel) {
            manejadorTablero.limpiarTablero()
            controladorPiezas.limpiarPiezaActiva()

            delayCaidaActualMs = maxOf(delayCaidaMinimo, delayCaidaActualMs - decrementoDelayCaidaPorNivel)

            detenerBucleJuego()

            if (controladorPiezas.generarNuevaPieza() == null) {
                terminarJuego()
            }

            notificarActualizacionPuntuacionNivel()

            if (!juegoTerminado) {
                iniciarBucleJuego()
            }
        }
    }

    private fun terminarJuego() {
        if (juegoTerminado) return
        juegoTerminado = true
        detenerBucleJuego()
        alTerminarJuego?.invoke(puntuacion)

        notificarActualizacionVista()
    }

    private fun notificarActualizacionVista() {
        alActualizarVistaJuego?.invoke()
    }

    private fun notificarActualizacionPuntuacionNivel() {
        alActualizarPuntuacionNivel?.invoke(puntuacion, nivel)
    }

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

    fun dejarCaerPiezaRapido() {
        if (juegoTerminado || controladorPiezas.piezaActiva == null) return

        detenerBucleJuego()
        controladorPiezas.dejarCaerPiezaRapido()

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

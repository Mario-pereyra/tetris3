package com.example.tetris.juego

import com.example.tetris.modelo.Punto
import com.example.tetris.modelo.TetrominoModelo
import com.example.tetris.modelo.TipoPieza
import kotlin.random.Random

/**
 * Gestiona la pieza activa del juego: su generación, movimiento, rotación
 * y la validación de estas acciones contra el tablero.
 *
 * @param anchoTablero El ancho del tablero, necesario para la posición inicial de la pieza.
 * @param manejadorTablero Referencia al [ManejadorTablero] para verificar colisiones.
 */
class ControladorPiezas(
    private val anchoTablero: Int,
    private val manejadorTablero: ManejadorTablero // Necesita conocer el tablero para validar movimientos
) {

    /**
     * La pieza (tetrominó) que el jugador está controlando actualmente.
     * Puede ser null si no hay pieza activa (ej. justo después de asentar una).
     */
    var piezaActiva: TetrominoModelo? = null
        private set

    /**
     * Genera una nueva pieza de tipo aleatorio y la posiciona en la parte superior
     * central del tablero.
     *
     * @return La [TetrominoModelo] recién creada, o null si la nueva pieza colisiona
     * inmediatamente al aparecer (condición de fin de juego).
     */
    fun generarNuevaPieza(): TetrominoModelo? {
        val tipoAleatorio = TipoPieza.values().random(Random(System.currentTimeMillis()))

        // Posición inicial para la nueva pieza.
        val xInicial = anchoTablero / 2 - 1 // Ajustar según pivotes de piezas I u O si es necesario
        var yInicial = 0

        // Ajuste para que piezas "altas" como la I vertical aparezcan completamente desde arriba.
        // Se calcula el bloque más "alto" (menor 'y' relativo al pivote) de la forma inicial.
        val piezaTemporalParaAltura = TetrominoModelo(tipoAleatorio, xInicial, yInicial)
        var yMinRelativoAlPivote = 0
        // Usamos la forma de la rotación inicial para el cálculo.
        val formaInicial = com.example.tetris.modelo.FormasPieza.formas[tipoAleatorio]?.get(0)
        formaInicial?.forEach { puntoRelativo ->
            if (puntoRelativo.y < yMinRelativoAlPivote) {
                yMinRelativoAlPivote = puntoRelativo.y
            }
        }
        // Si yMinRelativoAlPivote es -1 (ej. pieza I vertical con pivote en el segundo bloque),
        // la pieza debe empezar con su pivote en y=1 para que el bloque más alto esté en y=0.
        yInicial -= yMinRelativoAlPivote

        val nuevaPieza = TetrominoModelo(tipoAleatorio, xInicial, yInicial)

        // Comprueba si la nueva pieza colisiona al aparecer (indicando que no hay espacio).
        if (manejadorTablero.hayColision(nuevaPieza.obtenerCoordenadasAbsolutasDeBloques())) {
            piezaActiva = null // No se pudo generar
            return null // Condición de fin de juego
        }

        piezaActiva = nuevaPieza
        return piezaActiva
    }

    /**
     * Intenta mover la [piezaActiva] una posición hacia la izquierda.
     * @return true si el movimiento fue exitoso, false si hubo colisión.
     */
    fun moverPiezaIzquierda(): Boolean {
        piezaActiva?.let { pieza ->
            val proximasCoordenadas = pieza.obtenerCoordenadasAbsolutasDeBloques().map { Punto(it.x - 1, it.y) }
            if (!manejadorTablero.hayColision(proximasCoordenadas)) {
                pieza.moverHaciaIzquierda()
                return true
            }
        }
        return false
    }

    /**
     * Intenta mover la [piezaActiva] una posición hacia la derecha.
     * @return true si el movimiento fue exitoso, false si hubo colisión.
     */
    fun moverPiezaDerecha(): Boolean {
        piezaActiva?.let { pieza ->
            val proximasCoordenadas = pieza.obtenerCoordenadasAbsolutasDeBloques().map { Punto(it.x + 1, it.y) }
            if (!manejadorTablero.hayColision(proximasCoordenadas)) {
                pieza.moverHaciaDerecha()
                return true
            }
        }
        return false
    }

    /**
     * Intenta mover la [piezaActiva] una posición hacia abajo.
     * @return true si el movimiento fue exitoso, false si hubo colisión (la pieza debe asentarse).
     */
    fun moverPiezaAbajo(): Boolean {
        piezaActiva?.let { pieza ->
            val proximasCoordenadas = pieza.obtenerCoordenadasAbsolutasDeBloques().map { Punto(it.x, it.y + 1) }
            if (!manejadorTablero.hayColision(proximasCoordenadas)) {
                pieza.moverHaciaAbajo()
                return true // Movimiento exitoso
            }
        }
        return false // Hubo colisión, la pieza no se movió hacia abajo
    }

    /**
     * Intenta rotar la [piezaActiva] en sentido horario.
     * (Nota: no implementa "wall kicks" o "floor kicks" avanzados por defecto).
     * @return true si la rotación fue exitosa, false si hubo colisión.
     */
    fun rotarPieza(): Boolean {
        piezaActiva?.let { pieza ->
            val proximasCoordenadas = pieza.previsualizarCoordenadasSiguienteRotacion()
            if (!manejadorTablero.hayColision(proximasCoordenadas)) {
                pieza.rotarSentidoHorario()
                return true
            }
        }
        return false
    }

    /**
     * Mueve la [piezaActiva] instantáneamente hasta la posición más baja posible
     * donde colisionaría si bajara un paso más.
     * Actualiza la posición Y de la pieza activa.
     */
    fun dejarCaerPiezaRapido() {
        piezaActiva?.let { pieza ->
            var yCaidaFinal = pieza.y
            while (true) {
                // Obtenemos la forma relativa de la pieza en su rotación actual
                val formaRelativaActual = com.example.tetris.modelo.FormasPieza.formas[pieza.tipo]!![pieza.indiceRotacionActual]
                // Calculamos dónde estarían los bloques si la pieza estuviera en yCaidaFinal + 1
                val proximasCoordenadas = formaRelativaActual.map { puntoRelativo ->
                    Punto(puntoRelativo.x + pieza.x, puntoRelativo.y + yCaidaFinal + 1)
                }

                if (manejadorTablero.hayColision(proximasCoordenadas)) {
                    break // La posición yCaidaFinal es la última válida antes de la colisión.
                }
                yCaidaFinal++
            }
            pieza.y = yCaidaFinal // Mueve la pieza a la posición final calculada
        }
    }

    /**
     * Establece la pieza activa a null. Se usa después de que una pieza se asienta.
     */
    fun limpiarPiezaActiva() {
        piezaActiva = null
    }
}
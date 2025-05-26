package com.example.tetris.modelo

import android.graphics.Color // Para definir los colores de las piezas

/**
 * Representa una pieza de Tetris (un tetrominó) en el juego.
 *
 * @param tipo El [TipoPieza] de este tetrominó.
 * @param x La coordenada X actual del pivote de la pieza en el tablero.
 * @param y La coordenada Y actual del pivote de la pieza en el tablero.
 */
class TetrominoModelo(
    val tipo: TipoPieza,
    var x: Int,
    var y: Int
) {
    // Índice que rastrea el estado de rotación actual de la pieza.
    // Comienza en 0 (la primera forma definida en FormasPieza).
    internal var indiceRotacionActual: Int = 0

    // Obtiene la lista de todas las posibles formas (rotaciones) para este tipo de pieza
    // desde el objeto FormasPieza.
    // Si el tipo de pieza no existe en FormasPieza (lo cual no debería suceder si todo está bien definido),
    // se lanzará una IllegalArgumentException.
    private val todasLasRotaciones: List<Array<Punto>> = FormasPieza.formas[tipo]
        ?: throw IllegalArgumentException("Definición de forma no encontrada para el tipo de pieza: $tipo")

    // Determina el color de la pieza basado en su tipo.
    // Estos son colores estándar, pero puedes personalizarlos.
    val color: Int = when (tipo) {
        TipoPieza.I -> Color.CYAN
        TipoPieza.O -> Color.YELLOW
        TipoPieza.T -> Color.MAGENTA // Un color similar al morado
        TipoPieza.S -> Color.GREEN
        TipoPieza.Z -> Color.RED
        TipoPieza.J -> Color.BLUE
        TipoPieza.L -> Color.rgb(255, 165, 0) // Naranja (usando RGB)
    }

    /**
     * Devuelve una lista de objetos [Punto] que representan las coordenadas absolutas
     * (en el tablero de juego) de los cuatro bloques que componen esta pieza,
     * basada en su posición actual (x, y) y su estado de rotación actual.
     */
    fun obtenerCoordenadasAbsolutasDeBloques(): List<Punto> {
        val formaRelativaActual = todasLasRotaciones[indiceRotacionActual]
        // Para cada Punto en la forma relativa actual, calcula su posición absoluta
        // sumando las coordenadas del pivote de la pieza (this.x, this.y).
        return formaRelativaActual.map { puntoRelativo ->
            Punto(puntoRelativo.x + this.x, puntoRelativo.y + this.y)
        }
    }

    /**
     * Rota la pieza en el sentido de las agujas del reloj actualizando su
     * [indiceRotacionActual]. El índice se mueve al siguiente estado de rotación,
     * volviendo al primero (0) si alcanza el final de la lista de rotaciones.
     */
    fun rotarSentidoHorario() {
        indiceRotacionActual = (indiceRotacionActual + 1) % todasLasRotaciones.size
    }

    /**
     * Devuelve una lista de objetos [Punto] que representan las coordenadas absolutas
     * (en el tablero de juego) de los cuatro bloques que compondrían esta pieza
     * SI SE ROTARA al siguiente estado, sin aplicar realmente la rotación.
     * Esto es útil para verificar si una rotación es válida (por ejemplo, no causa colisiones)
     * antes de confirmarla.
     */
    fun previsualizarCoordenadasSiguienteRotacion(): List<Punto> {
        val proximoIndiceRotacion = (indiceRotacionActual + 1) % todasLasRotaciones.size
        val proximaFormaRelativa = todasLasRotaciones[proximoIndiceRotacion]
        return proximaFormaRelativa.map { puntoRelativo ->
            Punto(puntoRelativo.x + this.x, puntoRelativo.y + this.y)
        }
    }

    // Métodos para mover la pieza en el tablero.
    // Simplemente actualizan las coordenadas del pivote de la pieza.
    fun moverHaciaAbajo() { this.y++ }
    fun moverHaciaIzquierda() { this.x-- }
    fun moverHaciaDerecha() { this.x++ }



}

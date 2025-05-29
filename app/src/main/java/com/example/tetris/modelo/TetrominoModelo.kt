package com.example.tetris.modelo

import android.graphics.Color

class TetrominoModelo(
    val tipo: TipoPieza,
    var x: Int,
    var y: Int
) {

    internal var indiceRotacionActual: Int = 0

    private val todasLasRotaciones: List<Array<Punto>> = FormasPieza.formas[tipo]
        ?: throw IllegalArgumentException("Definición de forma no encontrada para el tipo de pieza: $tipo")

    val color: Int = when (tipo) {
        TipoPieza.I -> Color.CYAN
        TipoPieza.O -> Color.YELLOW
        TipoPieza.T -> Color.MAGENTA
        TipoPieza.S -> Color.GREEN
        TipoPieza.Z -> Color.RED
        TipoPieza.J -> Color.BLUE
        TipoPieza.L -> Color.rgb(255, 165, 0)
    }

    fun obtenerCoordenadasAbsolutasDeBloques(): List<Punto> {
        val formaRelativaActual = todasLasRotaciones[indiceRotacionActual]
        return formaRelativaActual.map { puntoRelativo ->
            Punto(puntoRelativo.x + this.x, puntoRelativo.y + this.y)
        }
    }

    fun rotarSentidoHorario() {
        indiceRotacionActual = (indiceRotacionActual + 1) % todasLasRotaciones.size
    }

    fun previsualizarCoordenadasSiguienteRotacion(): List<Punto> {
        val proximoIndiceRotacion = (indiceRotacionActual + 1) % todasLasRotaciones.size
        val proximaFormaRelativa = todasLasRotaciones[proximoIndiceRotacion]
        return proximaFormaRelativa.map { puntoRelativo ->
            Punto(puntoRelativo.x + this.x, puntoRelativo.y + this.y)
        }
    }
    fun moverHaciaAbajo() {
        this.y++
    }
    fun moverHaciaIzquierda() {
        this.x--
    }
    fun moverHaciaDerecha() {
        this.x++
    }
}

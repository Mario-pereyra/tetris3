package com.example.tetris.modelo

object FormasPieza {

    val formas: Map<TipoPieza, List<Array<Punto>>> = mapOf(
        TipoPieza.I to listOf(
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(0, 2)),
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(2, 0))
        ),
        TipoPieza.O to listOf(
            arrayOf(Punto(0, 0), Punto(1, 0), Punto(0, 1), Punto(1, 1))
        ),

        TipoPieza.T to listOf(
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(0, 1)),
            arrayOf(Punto(0, -1), Punto(-1, 0), Punto(0, 0), Punto(0, 1)),
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(0, -1)),
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(1, 0), Punto(0, 1))
        ),
        TipoPieza.L to listOf(
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(1, 1)),
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(-1, -1)),
            arrayOf(Punto(-1, -1), Punto(0, -1), Punto(0, 0), Punto(0, 1)),
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(1, 1))
        ),

        TipoPieza.J to listOf(
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(-1, 1)),
            arrayOf(Punto(-1, -1), Punto(-1, 0), Punto(0, 0), Punto(1, 0)),
            arrayOf(Punto(1, -1), Punto(0, -1), Punto(0, 0), Punto(0, 1)),
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(1, -1))
        ),

        TipoPieza.S to listOf(
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(0, 1), Punto(1, 1)),
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(1, 0), Punto(1, 1))
        ),

        TipoPieza.Z to listOf(
            arrayOf(Punto(0, 0), Punto(1, 0), Punto(-1, 1), Punto(0, 1)),
            arrayOf(Punto(1, -1), Punto(0, 0), Punto(1, 0), Punto(0, 1))
        )
    )
}


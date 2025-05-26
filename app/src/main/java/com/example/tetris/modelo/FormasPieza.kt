package com.example.tetris.modelo

/**
 * Objeto singleton que almacena las definiciones de las formas para cada tipo de pieza
 * y cada una de sus rotaciones.
 *
 * Cada forma se define como una lista de Puntos relativos a un pivote (0,0).
 * El Map 'formas' asocia un TipoPieza con una Lista de Arrays de Puntos (List<Array<Punto>>).
 * Cada Array<Punto> en esa lista representa un estado de rotación diferente para esa pieza.
 */
object FormasPieza {

    val formas: Map<TipoPieza, List<Array<Punto>>> = mapOf(
        TipoPieza.I to listOf(
            // Rotación 0: Vertical. Pivote en el segundo bloque desde arriba (contando desde 0).
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(0, 2)),
            // Rotación 1: Horizontal. Pivote en el segundo bloque desde la izquierda.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(2, 0))
        ),
        TipoPieza.O to listOf(
            // Rotación 0: Cuadrado. Pivote en la esquina superior izquierda.
            // La pieza O no cambia visualmente con la rotación.
            arrayOf(Punto(0, 0), Punto(1, 0), Punto(0, 1), Punto(1, 1))
        ),
        TipoPieza.T to listOf(
            // Rotación 0: Normal (palo hacia abajo). Pivote en el centro del travesaño.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(0, 1)),
            // Rotación 1: Rotada 90° derecha (palo hacia la izquierda). Pivote en el centro de la barra "vertical" de 3.
            arrayOf(Punto(0, -1), Punto(-1, 0), Punto(0, 0), Punto(0, 1)),
            // Rotación 2: Invertida (palo hacia arriba). Pivote en el centro del travesaño.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(0, -1)),
            // Rotación 3: Rotada 90° izquierda (palo hacia la derecha). Pivote en el centro de la barra "vertical" de 3.
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(1, 0), Punto(0, 1))
        ),
        TipoPieza.L to listOf(
            // Rotación 0: Forma de L base. Pivote en el "codo".
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(1, 1)),
            // Rotación 1: Rotada 90° derecha. Pivote en el bloque central de la barra horizontal.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(-1, -1)),
            // Rotación 2: L invertida verticalmente. Pivote en el "codo".
            arrayOf(Punto(-1, -1), Punto(0, -1), Punto(0, 0), Punto(0, 1)),
            // Rotación 3: Rotada 270° derecha. Pivote en el bloque central de la barra horizontal.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(1, 1))
        ),
        TipoPieza.J to listOf(
            // Rotación 0: Forma de J base. Pivote en el "codo".
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(0, 1), Punto(-1, 1)),
            // Rotación 1: Rotada 90° derecha. Pivote en el bloque central de la barra horizontal.
            arrayOf(Punto(-1, -1), Punto(-1, 0), Punto(0, 0), Punto(1, 0)),
            // Rotación 2: J invertida verticalmente. Pivote en el "codo".
            arrayOf(Punto(1, -1), Punto(0, -1), Punto(0, 0), Punto(0, 1)),
            // Rotación 3: Rotada 270° derecha. Pivote en el bloque central de la barra horizontal.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(1, 0), Punto(1, -1))
        ),
        TipoPieza.S to listOf(
            // Rotación 0: Horizontal. Pivote en el bloque derecho del par superior.
            arrayOf(Punto(-1, 0), Punto(0, 0), Punto(0, 1), Punto(1, 1)),
            // Rotación 1: Vertical. Pivote en el bloque inferior del par izquierdo.
            arrayOf(Punto(0, -1), Punto(0, 0), Punto(1, 0), Punto(1, 1))
        ),
        TipoPieza.Z to listOf(
            // Rotación 0: Horizontal. Pivote en el bloque izquierdo del par superior.
            arrayOf(Punto(0, 0), Punto(1, 0), Punto(-1, 1), Punto(0, 1)),
            // Rotación 1: Vertical. Pivote en el bloque superior del par izquierdo.
            arrayOf(Punto(1, -1), Punto(0, 0), Punto(1, 0), Punto(0, 1))
        )
    )
}
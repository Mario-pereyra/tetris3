package com.example.tetris.juego

import com.example.tetris.modelo.Punto
import com.example.tetris.modelo.TetrominoModelo

/**
 * Gestiona el estado del tablero de juego, incluyendo el asentamiento de piezas
 * y la eliminación de líneas completas.
 *
 * @param ancho El ancho del tablero en número de celdas.
 * @param alto El alto del tablero en número de celdas.
 */
class ManejadorTablero(val ancho: Int, val alto: Int) {

    /**
     * El tablero de juego. Es una matriz 2D donde cada celda puede contener:
     * - null: si la celda está vacía.
     * - Un Int (Color): si la celda está ocupada por un bloque de una pieza asentada.
     */
    var celdas: Array<Array<Int?>> = Array(alto) { arrayOfNulls<Int?>(ancho) }
        private set // Solo se puede modificar desde dentro de esta clase o por sus métodos.

    /**
     * Limpia el tablero, estableciendo todas las celdas a null.
     */
    fun limpiarTablero() {
        celdas = Array(alto) { arrayOfNulls<Int?>(ancho) }
    }

    /**
     * Asienta una pieza en el tablero, marcando las celdas correspondientes
     * con el color de la pieza.
     *
     * @param pieza La [TetrominoModelo] a asentar.
     * @return Boolean True si la pieza se asienta completamente dentro de los límites superiores del tablero,
     * False si alguna parte de la pieza se asienta por encima del límite superior (condición de Game Over).
     */
    fun asentarPieza(pieza: TetrominoModelo): Boolean {
        var piezaDentroDeLimitesSuperiores = true
        pieza.obtenerCoordenadasAbsolutasDeBloques().forEach { punto ->
            if (punto.y >= 0 && punto.y < alto && punto.x >= 0 && punto.x < ancho) {
                celdas[punto.y][punto.x] = pieza.color
            } else if (punto.y < 0) {
                // Si una parte de la pieza se asienta por encima del tablero visible (y < 0),
                // indica una condición de fin de juego.
                piezaDentroDeLimitesSuperiores = false
            }
            // Los bloques fuera de los límites laterales o inferiores no se asientan,
            // pero la lógica de colisión debería haber prevenido esto.
        }
        return piezaDentroDeLimitesSuperiores
    }

    /**
     * Verifica todas las líneas del tablero de abajo hacia arriba.
     * Si una línea está completa, la elimina y desplaza las líneas superiores hacia abajo.
     *
     * @return El número de líneas eliminadas en esta operación.
     */
    fun eliminarLineasCompletas(): Int {
        var lineasEliminadas = 0
        var y = alto - 1 // Empezar desde la fila inferior

        while (y >= 0) {
            if (esLineaCompleta(y)) {
                eliminarLineaYDesplazar(y)
                lineasEliminadas++
                // No decrementamos 'y' aquí, porque la línea que estaba arriba
                // ahora ocupa la posición 'y' y necesita ser revisada también.
            } else {
                y-- // Si la línea actual no está completa, pasar a la de arriba.
            }
        }
        return lineasEliminadas
    }

    /**
     * Comprueba si una fila específica del tablero está completamente llena de bloques.
     * @param indiceFila La fila a verificar.
     * @return true si la línea está completa, false en caso contrario.
     */
    private fun esLineaCompleta(indiceFila: Int): Boolean {
        if (indiceFila < 0 || indiceFila >= alto) return false // Índice fuera de rango
        for (x in 0 until ancho) {
            if (celdas[indiceFila][x] == null) {
                return false // Si encuentra una celda vacía, la línea no está completa.
            }
        }
        return true // Todas las celdas de la fila están ocupadas.
    }

    /**
     * Elimina una fila específica del tablero y desplaza todas las filas superiores hacia abajo.
     * La fila superior (fila 0) se rellena con celdas vacías (null).
     * @param indiceFilaAEliminar La fila a eliminar.
     */
    private fun eliminarLineaYDesplazar(indiceFilaAEliminar: Int) {
        if (indiceFilaAEliminar < 0 || indiceFilaAEliminar >= alto) return

        // Desplazar todas las filas por encima de la eliminada una posición hacia abajo.
        for (yDestino in indiceFilaAEliminar downTo 1) { // Desde la fila eliminada hasta la segunda fila (índice 1)
            val yOrigen = yDestino - 1
            for (x in 0 until ancho) {
                celdas[yDestino][x] = celdas[yOrigen][x] // Copia la celda de la fila de arriba a la actual
            }
        }

        // Limpiar la fila superior (fila 0), ya que ahora está vacía.
        for (x in 0 until ancho) {
            celdas[0][x] = null
        }
    }

    /**
     * Verifica si las coordenadas de bloques propuestas colisionarían con los límites
     * del tablero o con piezas ya asentadas en el tablero.
     * @param coordenadasBloques Lista de [Punto]s representando las posiciones a verificar.
     * @return true si hay colisión, false en caso contrario.
     */
    fun hayColision(coordenadasBloques: List<Punto>): Boolean {
        for (punto in coordenadasBloques) {
            // 1. Colisión con bordes laterales
            if (punto.x < 0 || punto.x >= ancho) {
                return true
            }
            // 2. Colisión con el borde inferior
            if (punto.y >= alto) {
                return true
            }
            // 3. Colisión con piezas ya asentadas en el tablero.
            //    Solo se considera colisión si el bloque está dentro de la parte visible vertical del tablero (y >= 0).
            //    Si y < 0, el bloque está por encima del tablero, lo cual es permitido durante la aparición o movimiento.
            if (punto.y >= 0 && celdas[punto.y][punto.x] != null) {
                return true
            }
        }
        return false // No se detectó ninguna colisión.
    }
}
package com.example.tetris.juego

import com.example.tetris.modelo.Punto
import com.example.tetris.modelo.TetrominoModelo

class ManejadorTablero(val ancho: Int, val alto: Int) {

    var celdas: Array<Array<Int?>> = Array(alto) { arrayOfNulls<Int?>(ancho) }
        private set

    fun limpiarTablero() {
        celdas = Array(alto) { arrayOfNulls<Int?>(ancho) }
    }

    fun asentarPieza(pieza: TetrominoModelo): Boolean {
        var piezaDentroDeLimitesSuperiores = true
        pieza.obtenerCoordenadasAbsolutasDeBloques().forEach { punto ->
            if (punto.y >= 0 && punto.y < alto && punto.x >= 0 && punto.x < ancho) {
                celdas[punto.y][punto.x] = pieza.color
            } else if (punto.y < 0) {
                piezaDentroDeLimitesSuperiores = false
            }
        }
        return piezaDentroDeLimitesSuperiores
    }

    fun eliminarLineasCompletas(): Int {
        var lineasEliminadas = 0
        var y = alto - 1

        while (y >= 0) {
            if (esLineaCompleta(y)) {
                eliminarLineaYDesplazar(y)
                lineasEliminadas++
            } else {
                y--
            }
        }
        return lineasEliminadas
    }

    private fun esLineaCompleta(indiceFila: Int): Boolean {
        if (indiceFila < 0 || indiceFila >= alto) return false
        for (x in 0 until ancho) {
            if (celdas[indiceFila][x] == null) {
                return false
            }
        }
        return true
    }

    private fun eliminarLineaYDesplazar(indiceFilaAEliminar: Int) {
        if (indiceFilaAEliminar < 0 || indiceFilaAEliminar >= alto) return

        for (yDestino in indiceFilaAEliminar downTo 1) {
            val yOrigen = yDestino - 1
            for (x in 0 until ancho) {
                celdas[yDestino][x] = celdas[yOrigen][x]
            }
        }

        for (x in 0 until ancho) {
            celdas[0][x] = null
        }
    }

    fun hayColision(coordenadasBloques: List<Punto>): Boolean {
        for (punto in coordenadasBloques) {
            if (punto.x < 0 || punto.x >= ancho) {
                return true
            }

            if (punto.y >= alto) {
                return true
            }

            if (punto.y >= 0 && celdas[punto.y][punto.x] != null) {
                return true
            }
        }
        return false
    }
}


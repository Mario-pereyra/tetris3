package com.example.tetris.juego

import com.example.tetris.modelo.FormasPieza
import com.example.tetris.modelo.Punto
import com.example.tetris.modelo.TetrominoModelo
import com.example.tetris.modelo.TipoPieza
import kotlin.random.Random

class ControladorPiezas(
    private val anchoTablero: Int,
    private val manejadorTablero: ManejadorTablero
) {

    var piezaActiva: TetrominoModelo? = null
        private set

    fun generarNuevaPieza(): TetrominoModelo? {
        val tipoAleatorio = TipoPieza.values().random(Random(System.currentTimeMillis()))

        val xInicial = anchoTablero / 2 - 1
        var yInicial = 0

        var yMinRelativoAlPivote = 0

        val formaInicial = FormasPieza.formas[tipoAleatorio]?.get(0)
        formaInicial?.forEach { puntoRelativo ->
            if (puntoRelativo.y < yMinRelativoAlPivote) {
                yMinRelativoAlPivote = puntoRelativo.y
            }
        }

        yInicial -= yMinRelativoAlPivote

        val nuevaPieza = TetrominoModelo(tipoAleatorio, xInicial, yInicial)

        if (manejadorTablero.hayColision(nuevaPieza.obtenerCoordenadasAbsolutasDeBloques())) {
            piezaActiva = null
            return null
        }

        piezaActiva = nuevaPieza
        return piezaActiva
    }

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

    fun moverPiezaAbajo(): Boolean {
        piezaActiva?.let { pieza ->
            val proximasCoordenadas = pieza.obtenerCoordenadasAbsolutasDeBloques().map { Punto(it.x, it.y + 1) }
            if (!manejadorTablero.hayColision(proximasCoordenadas)) {
                pieza.moverHaciaAbajo()
                return true
            }
        }
        return false
    }

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

    fun dejarCaerPiezaRapido() {
        piezaActiva?.let { pieza ->
            var yCaidaFinal = pieza.y
            while (true) {
                val formaRelativaActual = FormasPieza.formas[pieza.tipo]!![pieza.indiceRotacionActual]
                val proximasCoordenadas = formaRelativaActual.map { puntoRelativo ->
                    Punto(puntoRelativo.x + pieza.x, puntoRelativo.y + yCaidaFinal + 1)
                }

                if (manejadorTablero.hayColision(proximasCoordenadas)) {
                    break
                }
                yCaidaFinal++
            }
            pieza.y = yCaidaFinal
        }
    }

    fun limpiarPiezaActiva() {
        piezaActiva = null
    }
}


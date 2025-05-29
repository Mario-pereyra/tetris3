package com.example.tetris.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tetris.db.PuntuacionDao
import com.example.tetris.db.PuntuacionEntity
import com.example.tetris.juego.MotorJuegoTetris
import com.example.tetris.modelo.TetrominoModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TetrisViewModel(private val puntuacionDao: PuntuacionDao) : ViewModel() {


    private val motorJuego: MotorJuegoTetris = MotorJuegoTetris(anchoTablero = 10, altoTablero = 16)

    private val _estadoTablero = MutableLiveData<Array<Array<Int?>>>()
    val estadoTablero: LiveData<Array<Array<Int?>>> = _estadoTablero

    private val _piezaActual = MutableLiveData<TetrominoModelo?>()
    val piezaActual: LiveData<TetrominoModelo?> = _piezaActual


    private val _puntuacion = MutableLiveData(0)
    val puntuacion: LiveData<Int> = _puntuacion

    private val _nivel = MutableLiveData(1)
    val nivel: LiveData<Int> = _nivel

    private val _juegoTerminado = MutableLiveData(false)
    val juegoTerminado: LiveData<Boolean> = _juegoTerminado

    private val _top10Puntuaciones = MutableLiveData<List<PuntuacionEntity>>()
    val top10Puntuaciones: LiveData<List<PuntuacionEntity>> = _top10Puntuaciones

    init {

        configurarListenersMotor()


        reiniciarJuego()
    }

    private fun configurarListenersMotor() {
        motorJuego.alActualizarVistaJuego = {

            _estadoTablero.postValue(motorJuego.celdasTablero)
            _piezaActual.postValue(motorJuego.piezaActualParaVista)
        }

        motorJuego.alActualizarPuntuacionNivel = { puntuacion, nivel ->
            _puntuacion.postValue(puntuacion)
            _nivel.postValue(nivel)
        }

        motorJuego.alTerminarJuego = { puntuacionFinal ->
            _juegoTerminado.postValue(true)
        }
    }



    fun moverPiezaIzquierda() {
        motorJuego.moverPiezaIzquierda()
    }

    fun moverPiezaDerecha() {
        motorJuego.moverPiezaDerecha()
    }

    fun rotarPieza() {
        motorJuego.rotarPieza()
    }

    fun dejarCaerPiezaRapido() {
        motorJuego.dejarCaerPiezaRapido()
    }

    fun reiniciarJuego() {
        motorJuego.reiniciarJuego()
        _juegoTerminado.postValue(false)
    }



    fun guardarPuntuacion(nombre: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val puntuacionActual = puntuacion.value ?: 0
                puntuacionDao.insertarPuntuacion(
                    PuntuacionEntity(nombre = nombre, puntuacion = puntuacionActual)
                )
            }
        }
    }

    fun cargarTop10Puntuaciones() {
        viewModelScope.launch {
            val puntuaciones = withContext(Dispatchers.IO) {
                puntuacionDao.obtenerTop10()
            }
            _top10Puntuaciones.postValue(puntuaciones)
        }
    }
}

package com.example.tetris.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tetris.db.PuntuacionDao

/**
 * Factory para crear instancias de TetrisViewModel con dependencias.
 */
class TetrisViewModelFactory(private val puntuacionDao: PuntuacionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TetrisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TetrisViewModel(puntuacionDao) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}

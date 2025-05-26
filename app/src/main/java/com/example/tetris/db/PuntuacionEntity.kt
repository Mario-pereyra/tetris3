package com.example.tetris.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntuaciones")
data class PuntuacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val puntuacion: Int
)

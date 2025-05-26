package com.example.tetris.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PuntuacionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPuntuacion(puntuacion: PuntuacionEntity)

    @Query("SELECT * FROM puntuaciones ORDER BY puntuacion DESC, id ASC LIMIT 10")
    suspend fun obtenerTop10(): List<PuntuacionEntity>
}

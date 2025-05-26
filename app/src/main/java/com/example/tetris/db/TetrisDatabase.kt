package com.example.tetris.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PuntuacionEntity::class], version = 1, exportSchema = false)
abstract class TetrisDatabase : RoomDatabase() {
    abstract fun puntuacionDao(): PuntuacionDao

    companion object {
        @Volatile
        private var INSTANCE: TetrisDatabase? = null

        fun getInstance(context: Context): TetrisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TetrisDatabase::class.java,
                    "tetris_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

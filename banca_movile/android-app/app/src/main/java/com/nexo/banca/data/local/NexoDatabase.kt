package com.nexo.banca.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AccountEntity::class, MovementEntity::class, BillEntity::class, CardSettingsEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NexoDatabase : RoomDatabase() {
    abstract fun dao(): NexoDao

    companion object {
        @Volatile
        private var instance: NexoDatabase? = null

        fun getInstance(context: Context): NexoDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NexoDatabase::class.java,
                "nexo_demo.db"
            ).build().also { instance = it }
        }
    }
}

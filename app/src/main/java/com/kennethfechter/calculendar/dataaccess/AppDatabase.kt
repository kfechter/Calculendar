package com.kennethfechter.calculendar.dataaccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Calculation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val calculationDao: CalculationDao

    companion object {
        private const val DB_NAME = "calculation.db"

        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if(instance == null) {
                instance = create(context)
            }

            return instance
        }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}
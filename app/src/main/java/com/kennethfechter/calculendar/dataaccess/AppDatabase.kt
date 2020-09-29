package com.kennethfechter.calculendar.dataaccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Calculation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val calculationDao: CalculationDao

    companion object {
        var TEST_MODE = false
        private const val DB_NAME = "calculation.db"

        @Volatile
        private var db: AppDatabase? = null

        @Volatile
        private var instance: CalculationDao? = null

        @Synchronized
        fun getInstance(context: Context): CalculationDao? {
            if(instance == null) {
                instance = create(context, TEST_MODE)
            }

            return instance!!
        }

        private fun create(context: Context, testMode: Boolean): CalculationDao? {
            return if (testMode) {
                db = Room.inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java
                ).allowMainThreadQueries().build()
                db?.calculationDao
            } else {
                db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                db?.calculationDao
            }
        }

        private fun close() {
            db?.close()
        }
    }
}
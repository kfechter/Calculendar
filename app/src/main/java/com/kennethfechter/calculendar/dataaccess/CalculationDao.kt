package com.kennethfechter.calculendar.dataaccess

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CalculationDao {
    @Query("select * from calculation")
    fun getAll(): LiveData<List<Calculation>>

    @Query("SELECT * FROM calculation WHERE uid IN (:calculationIds)")
    fun getAllByIds(calculationIds: IntArray): LiveData<List<Calculation>>

    @Query("SELECT * FROM calculation WHERE uid = :calculationId")
    fun getByID(calculationId: Int) : LiveData<Calculation>

    @Query("SELECT * FROM calculation WHERE start_date = :startDate LIMIT 1")
    fun getByStartDate(startDate: String): LiveData<Calculation>

    @Query("DELETE FROM calculation")
    fun deleteAll()

    @Query("DELETE FROM calculation WHERE uid = :calculationId")
    suspend fun deleteById(calculationId: Int)

    @Query("SELECT COUNT(*) FROM calculation")
    fun getCalculationCount() : Int

    @Insert
    suspend fun insert(vararg calculation: Calculation)

    @Delete
    fun delete(calculation: Calculation)
}
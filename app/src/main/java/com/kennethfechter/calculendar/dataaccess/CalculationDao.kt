package com.kennethfechter.calculendar.dataaccess

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CalculationDao {
    @Query("select * from calculation")
    fun getAll(): List<Calculation>

    @Query("SELECT * FROM calculation WHERE uid IN (:calculationIds)")
    fun getAllByIds(calculationIds: IntArray): LiveData<List<Calculation>>

    @Query("SELECT * FROM calculation WHERE uid = :calculationId")
    fun getByID(calculationId: Int) : LiveData<Calculation>

    @Query("DELETE FROM calculation")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM calculation")
    fun getCalculationCount() : Int

    // fun filterByDate() // Get The item before or after a date?

    @Insert
    fun insertAll(vararg calculations: Calculation)

    @Delete
    fun delete(calculation: Calculation)

    // delete by age
}
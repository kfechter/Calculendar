package com.kennethfechter.calculendar.dataaccess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Calculation (
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    @ColumnInfo(name = "start_date") val startDate: String?,
    @ColumnInfo(name = "end_date") val endDate: String?,
    @ColumnInfo(name = "exclusion_method") val exclusionMethod: String?,
    @ColumnInfo(name = "custom_dates") val customDates: String?,
    @ColumnInfo(name = "excluded_dates") val numExcludedDates: Int?,
    @ColumnInfo(name = "calculated_interval") val calculatedInterval: Int?
)
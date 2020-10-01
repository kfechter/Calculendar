package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.Calculation
import com.kennethfechter.calculendar.dataaccess.CalculationDao
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import java.util.*

object DateCalculator {
    private var calculationDao: CalculationDao? = null

    fun CalculateInterval(context: Context, selectedDates: MutableList<Date>, customDateExclusions: MutableList<Date>, exclusionMode: ExclusionMode, storeResult: Boolean) : String {

        var calculatedDays: Int = selectedDates.size
        val excludedDays: Int

        var numSaturdays = 0
        var numSundays = 0

        val iterator = selectedDates.listIterator()
        val calendar = Calendar.getInstance()

        for (item in iterator) {
            calendar.time = item

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> numSaturdays++
                Calendar.SUNDAY -> numSundays++
            }
        }

        excludedDays = when (exclusionMode) {
            ExclusionMode.Sundays -> numSundays
            ExclusionMode.Saturdays -> numSaturdays
            ExclusionMode.Both -> (numSaturdays + numSundays)
            ExclusionMode.CustomDates -> customDateExclusions.size
            ExclusionMode.None -> 0
        }

        calculatedDays -= excludedDays

        val startDate = Converters.convertDateToString(selectedDates[0])
        val endDate = Converters.convertDateToString(selectedDates[selectedDates.size - 1])
        val resultPlural = context.resources.getQuantityString(R.plurals.calculated_days, calculatedDays)

        val result = context.resources.getString(R.string.calculation_result_formatter)
            .format(startDate, endDate, excludedDays, calculatedDays, resultPlural)

        if (storeResult) {
            calculationDao = AppDatabase.getInstance(context)
            val calculation = Calculation(
                startDate = startDate,
                endDate = endDate,
                customDates = when (exclusionMode) {
                    ExclusionMode.CustomDates -> Converters.getCommaSeperatedExcludedDatesList(customDateExclusions)
                    else -> null
                },
                numExcludedDates = excludedDays,
                calculatedInterval = calculatedDays,
                exclusionMethod = exclusionMode.toString()
            )

            calculationDao?.insert(calculation)
        }

        return result
    }

    fun StartCalculation() {
        // Show Date Range Picker Dialog
        // Show Custom Dialog with Exclusion Options
        // Show Result
        // Store
    }
}
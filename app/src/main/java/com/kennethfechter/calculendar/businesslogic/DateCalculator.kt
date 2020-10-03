package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import android.os.AsyncTask
import android.provider.Settings
import android.widget.Toast
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.Calculation
import com.kennethfechter.calculendar.dataaccess.CalculationDao
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                    ExclusionMode.CustomDates -> Converters.getCommaSeparatedExcludedDatesList(customDateExclusions)
                    else -> null
                },
                numExcludedDates = excludedDays,
                calculatedInterval = calculatedDays,
                exclusionMethod = exclusionMode.toString()
            )

            GlobalScope.launch(Dispatchers.IO) {
                calculationDao?.insert(calculation)
            }
        }

        return result
    }

    fun startCalculation(context: Context) {
        var selectedDates: MutableList<Date>
        var excludedDates: MutableList<Date>
        var exclusionMode: ExclusionMode

        GlobalScope.launch(Dispatchers.Main) {
            selectedDates = Dialogs.showDatePickerDialog(context, context.resources.getString(R.string.date_picker_dialog_title),true)

            if (selectedDates.size > 1) {
                val exclusionOptions: Pair<MutableList<Date>, ExclusionMode>? = Dialogs.showExclusionOptionsDialog(context, Converters.getSelectedRangeString(selectedDates), selectedDates)
                if (exclusionOptions != null) {
                    excludedDates = exclusionOptions.first
                    exclusionMode = exclusionOptions.second

                    val result = CalculateInterval(context, selectedDates, excludedDates, exclusionMode, true)
                    Dialogs.showResultDialog(context, result)
                }
                else {
                    Dialogs.showToastPrompt(context, "Date Calculation Canceled", Toast.LENGTH_LONG)
                }
            }
            else {
                Dialogs.showToastPrompt(context, "A valid date range was not selected", Toast.LENGTH_LONG)
            }
        }
    }
}
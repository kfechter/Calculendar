package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import com.kennethfechter.calculendar.R
import java.text.SimpleDateFormat
import java.util.*

object Converters {
    fun getSelectedRangeString(selectedDates: MutableList<Date>): String {
        if (selectedDates.size < 2 ) { return "Invalid Range" }
        return "%s - %s".format(convertDateToString(selectedDates[0]), convertDateToString(selectedDates[selectedDates.size -1]))
    }

    fun convertDateToString(selectedDate: Date) : String {
        return SimpleDateFormat("EEEE MMM d, yyyy", Locale.getDefault()).format(selectedDate)
    }

    fun getFormattedCustomDateString(context: Context, numberOfCustomDates: Int) : String {
        val customDatePlural = context.resources.getQuantityString(R.plurals.custom_dates, numberOfCustomDates)
        return context.resources.getString(R.string.custom_dates_formatter).format(numberOfCustomDates, customDatePlural)
    }
}
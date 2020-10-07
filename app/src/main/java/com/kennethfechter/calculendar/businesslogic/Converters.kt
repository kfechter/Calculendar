package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.dataaccess.Calculation
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

    fun getFormattedDateFromStringDate(dateRepresentation: String): String {
        return convertDateToString(Date(dateRepresentation.toLong()))
    }

    fun getCommaSeparatedExcludedDatesList(excludedDatesList: MutableList<Date>) : String {
        val convertedDatesList: MutableList<String> = mutableListOf()
        val iterator = excludedDatesList.listIterator()
        for (date in iterator) {
            convertedDatesList.add(date.time.toString())
        }

        return convertedDatesList.joinToString(",")
    }

    fun getCustomDateStringList(customDateCSV: String?) : MutableList<String>? {
        val convertedDates = customDateCSV?.split(",")?.toMutableList()
        val iterator = convertedDates?.iterator()!!

        val convertedDatesList: MutableList<String> = mutableListOf()

        for(stringDate in iterator) {
            convertedDatesList.add(getFormattedDateFromStringDate(stringDate))
        }

        return convertedDatesList
    }
}
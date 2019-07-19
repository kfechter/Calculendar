package com.kennethfechter.calculendar3.businesslogic

import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun getSelectedRangeString(selectedDates: MutableList<Date>): String {
        return "%s - %s".format(convertDateToString(selectedDates[0]), convertDateToString(selectedDates[selectedDates.size -1]))
    }

    fun convertDateToString(selectedDate: Date) : String {
        return SimpleDateFormat("EEEE MMM d, yyyy", Locale.getDefault()).format(selectedDate)
    }
}


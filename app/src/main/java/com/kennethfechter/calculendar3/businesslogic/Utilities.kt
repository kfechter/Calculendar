package com.kennethfechter.calculendar3.businesslogic

import android.content.Context
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun getSelectedRangeString(selectedDates: MutableList<Date>): String {
        return "%s - %s".format(convertDateToString(selectedDates[0]), convertDateToString(selectedDates[selectedDates.size -1]))
    }

    fun convertDateToString(selectedDate: Date) : String {
        return SimpleDateFormat("EEEE MMM d, yyyy", Locale.getDefault()).format(selectedDate)
    }

    fun getPackageVersionName(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            return ""
        }

    }

    fun calculateDays(selectedDates: MutableList<Date>, customDateExclusions: MutableList<Date>, exclusionMethod: String) : Int {

        var calculatedDays: Int = selectedDates.size

        var saturdays = 0
        var sundays = 0

        val iterator = selectedDates.listIterator()
        for(item in iterator) {
            var calendar = Calendar.getInstance()
            calendar.time = item

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                saturdays++
            }

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sundays++
            }
        }

        calculatedDays -= when(exclusionMethod) {
            "Sundays" -> sundays
            "Saturdays" -> saturdays
            "Both" -> (saturdays + sundays)
            "Custom" -> customDateExclusions.size
            else -> 0
        }

        return calculatedDays
    }
}


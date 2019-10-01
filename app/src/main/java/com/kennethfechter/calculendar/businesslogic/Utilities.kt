package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import com.kennethfechter.calculendar.R
import com.squareup.timessquare.CalendarPickerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    fun getCustomDatesFormatterString(context: Context, customDates: Int): String {
        val customDatePlural = context.resources.getQuantityString(R.plurals.custom_dates, customDates)

        return context.resources.getString(R.string.custom_dates_formatter).format(customDates, customDatePlural)
    }

    fun calculateDays(context: Context, selectedDates: MutableList<Date>, customDateExclusions: MutableList<Date>, exclusionMethod: String) : String {

        var calculatedDays: Int = selectedDates.size

        var excludedDays: Int

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

        excludedDays = when(exclusionMethod) {
            "Exclude Sundays" -> sundays
            "Exclude Saturdays" -> saturdays
            "Exclude Both" -> (saturdays + sundays)
            "Exclude Custom" -> customDateExclusions.size
            else -> 0
        }

        calculatedDays -= excludedDays

        val startDate = convertDateToString(selectedDates[0])
        val endDate = convertDateToString(selectedDates[selectedDates.size -1])
        val calculationPlural = context.resources.getQuantityString(R.plurals.calculated_days, calculatedDays)

        return context.resources.getString(R.string.calculation_result_formatter)
            .format(startDate, endDate, excludedDays, calculatedDays, calculationPlural)
    }

    suspend fun displayDatePickerDialog(context: Context, dialogTitle: String, rangeSelectionMode: Boolean, selectedDates: MutableList<Date> = mutableListOf(), excludedDates: MutableList<Date> = mutableListOf()) = suspendCoroutine<MutableList<Date>> {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calculendar_datepicker, null)
        val calendarPicker: CalendarPickerView = dialogView.findViewById(R.id.calendar_view)

        if(rangeSelectionMode) {
            val pastDate = Calendar.getInstance()
            val futureDate = Calendar.getInstance()
            futureDate.add(Calendar.YEAR, 1)
            pastDate.add(Calendar.YEAR, -1)
            val today = Date()

            calendarPicker.init(pastDate.time, futureDate.time)
                .inMode(CalendarPickerView.SelectionMode.RANGE)

            calendarPicker.selectDate(today, true)
        } else {
            calendarPicker.init(selectedDates[0], selectedDates[selectedDates.size -1])
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(excludedDates)
        }

        AlertDialog.Builder(context)
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton("Select") {
                dialog, _ ->
                dialog.dismiss()
                it.resume(calendarPicker.selectedDates)
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                dialog.dismiss()
                it.resume(mutableListOf())
            }
            .create()
            .show()
    }
}


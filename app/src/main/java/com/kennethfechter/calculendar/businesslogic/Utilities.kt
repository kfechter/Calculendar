package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import com.kennethfechter.calculendar.R
import com.squareup.timessquare.CalendarPickerView
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Utilities {

    val isRunningTest : Boolean by lazy {
        try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    fun getPackageVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    // Utilities below here should be moved and re-written

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


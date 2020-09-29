package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
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

    // Utilities below here should be moved and re-written

    fun getPackageVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    fun getCustomDatesFormatterString(context: Context, customDates: Int): String {
        val customDatePlural = context.resources.getQuantityString(R.plurals.custom_dates, customDates)

        return context.resources.getString(R.string.custom_dates_formatter).format(customDates, customDatePlural)
    }

    fun calculateDays(context: Context, selectedDates: MutableList<Date>, customDateExclusions: MutableList<Date>, exclusionMethod: String) : String {

        var calculatedDays: Int = selectedDates.size

        val excludedDays: Int

        var saturdays = 0
        var sundays = 0

        val iterator = selectedDates.listIterator()
        for(item in iterator) {
            val calendar = Calendar.getInstance()
            calendar.time = item

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                saturdays++
            }

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sundays++
            }
        }

        // TODO: Allow Custom AND Saturdays/Sundays?
        excludedDays = when (exclusionMethod) {
            "Exclude Sundays" -> sundays
            "Exclude Saturdays" -> saturdays
            "Exclude Both" -> (saturdays + sundays)
            "Exclude Custom" -> customDateExclusions.size
            else -> 0
        }

        calculatedDays -= excludedDays

        val startDate = Converters.convertDateToString(selectedDates[0])
        val endDate = Converters.convertDateToString(selectedDates[selectedDates.size -1])
        val calculationPlural = context.resources.getQuantityString(R.plurals.calculated_days, calculatedDays)

        return context.resources.getString(R.string.calculation_result_formatter)
            .format(startDate, endDate, excludedDays, calculatedDays, calculationPlural)
    }

    fun updateBooleanSharedPref(context: Context, prefKey: String, prefValue: Boolean) {
        val preferenceFileName = context.getString(R.string.shared_preference_file_name)
        val sharedPrefs = context.getSharedPreferences(preferenceFileName, 0)
        val preferenceEditor = sharedPrefs!!.edit()

        preferenceEditor.putBoolean(prefKey, prefValue)
        preferenceEditor.apply()
    }

    fun retrieveBooleanSharedPref(context: Context, prefKey: String, defaultValue: Boolean) : Boolean {
        val preferenceFileName = context.getString(R.string.shared_preference_file_name)
        val sharedPrefs = context.getSharedPreferences(preferenceFileName, 0)
        return sharedPrefs!!.getBoolean(prefKey, defaultValue)
    }

    private fun updateStringSharedPreference(context: Context, prefKey: String, prefValue: String) {
        val preferenceFileName = context.getString(R.string.shared_preference_file_name)
        val sharedPrefs = context.getSharedPreferences(preferenceFileName, 0)
        val preferenceEditor = sharedPrefs!!.edit()

        preferenceEditor.putString(prefKey, prefValue)
        preferenceEditor.apply()
    }

    fun retrieveStringSharedPreference(context:Context, prefKey: String, defaultValue: String) : String {
        val preferenceFileName = context.getString(R.string.shared_preference_file_name)
        val sharedPrefs = context.getSharedPreferences(preferenceFileName, 0)
        return sharedPrefs!!.getString(prefKey, defaultValue)!!
    }

    fun getDayNightMode(context: Context) : Int {
        val dayNightPreferenceName = context.getString(R.string.day_night_preference_name)
        val currentPreferenceValue = retrieveStringSharedPreference(context, dayNightPreferenceName, "")
        val dayNightAutoPreference = context.getString(R.string.preference_auto_mode_value)
        if(currentPreferenceValue == "") {
            updateStringSharedPreference(context, dayNightPreferenceName, dayNightAutoPreference)
            return AppCompatDelegate.getDefaultNightMode()
        }

        return when(currentPreferenceValue) {
            context.getString(R.string.preference_day_mode_value) -> AppCompatDelegate.MODE_NIGHT_NO
            context.getString(R.string.preference_night_mode_value) -> AppCompatDelegate.MODE_NIGHT_YES
            context.getString(R.string.preference_battery_saver_mode_value) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            context.getString(R.string.preference_auto_mode_value) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.getDefaultNightMode()
        }
    }

    private fun setNightMode(context: Context, dayNightMode: Int){
        val dayNightPreferenceName = context.getString(R.string.day_night_preference_name)
        val autoModeDefaultCase = when(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            true -> context.getString(R.string.preference_auto_mode_value)
            else -> context.getString(R.string.preference_day_mode_value)
        }
        val desiredDayNightPreference = when(dayNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> context.getString(R.string.preference_day_mode_value)
            AppCompatDelegate.MODE_NIGHT_YES -> context.getString(R.string.preference_night_mode_value)
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> context.getString(R.string.preference_battery_saver_mode_value)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> context.getString(R.string.preference_auto_mode_value)
            else -> autoModeDefaultCase
        }

        updateStringSharedPreference(context, dayNightPreferenceName, desiredDayNightPreference)
        AppCompatDelegate.setDefaultNightMode(dayNightMode)
    }

    fun showDayNightModeDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calculendar_daynight_mode, null)
        var preferredDayNightMode = getDayNightMode(context)

        when(preferredDayNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> dialogView.findViewById<RadioButton>(R.id.radio_day_mode).isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> dialogView.findViewById<RadioButton>(R.id.radio_night_mode).isChecked = true
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> dialogView.findViewById<RadioButton>(R.id.radio_battery_mode).isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> dialogView.findViewById<RadioButton>(R.id.radio_auto_mode).isChecked = true
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dialogView.findViewById<RadioButton>(R.id.radio_auto_mode).visibility = View.VISIBLE

            dialogView.findViewById<RadioButton>(R.id.radio_auto_mode).setOnCheckedChangeListener {
                    _, isChecked -> if(isChecked) { preferredDayNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }}
        }

        dialogView.findViewById<RadioButton>(R.id.radio_day_mode).setOnCheckedChangeListener {
            _, isChecked -> if(isChecked) { preferredDayNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }}

        dialogView.findViewById<RadioButton>(R.id.radio_night_mode).setOnCheckedChangeListener {
                _, isChecked -> if(isChecked) { preferredDayNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }}

        dialogView.findViewById<RadioButton>(R.id.radio_battery_mode).setOnCheckedChangeListener {
                _, isChecked -> if(isChecked) { preferredDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }}

        builder.setTitle(R.string.theme_dialog_title)
        builder.setView(dialogView)

        builder.setPositiveButton("OK") { dialog, _ ->
            setNightMode(context, preferredDayNightMode)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    suspend fun displayAnalyticsOptInDialog(context: Context) = suspendCoroutine<String> {
        val analyticsPreferenceName = context.getString(R.string.preference_name_analytics_level)

        val firstRunPreferenceName = context.getString(R.string.first_run_preference_name)
        val builder = AlertDialog.Builder(context)

        val fullAnalyticsButton = context.getString(R.string.dialog_button_full_analytics)
        val crashOnlyAnalyticsButton = context.getString(R.string.dialog_button_crash_only)
        val optOutButton = context.getString(R.string.dialog_button_opt_out)


        builder.setTitle(R.string.opt_in_dialog_title)
        builder.setMessage(R.string.opt_in_dialog_message)

        builder.setPositiveButton(fullAnalyticsButton) { dialog, _ ->
            val fullAnalyticsValue = context.getString(R.string.full_analytics_preference_value)
            updateStringSharedPreference(context, analyticsPreferenceName, fullAnalyticsValue)
            updateBooleanSharedPref(context, firstRunPreferenceName, false)
            dialog.dismiss()
            it.resume(fullAnalyticsValue)
        }

        builder.setNegativeButton(crashOnlyAnalyticsButton) { dialog, _ ->
            val crashAnalyticsValue = context.getString(R.string.crash_only_analytics_preference_value)
            updateStringSharedPreference(context, analyticsPreferenceName, crashAnalyticsValue)
            updateBooleanSharedPref(context, firstRunPreferenceName, false)
            dialog.dismiss()
            it.resume(crashAnalyticsValue)
        }

        builder.setNeutralButton(optOutButton) { dialog, _ ->
            val noAnalyticsValue = context.getString(R.string.no_analytics_preference_value)
            updateStringSharedPreference(context, analyticsPreferenceName, noAnalyticsValue)
            updateBooleanSharedPref(context, firstRunPreferenceName, false)
            dialog.dismiss()
            it.resume(noAnalyticsValue)
        }

        builder.create().show()
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


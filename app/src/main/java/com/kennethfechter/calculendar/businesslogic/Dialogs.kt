package com.kennethfechter.calculendar.businesslogic

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import com.kennethfechter.calculendar.enumerations.Theme
import com.kennethfechter.calculendar.views.CalculationDetailFragment
import com.squareup.timessquare.CalendarPickerView
import kotlinx.android.synthetic.main.activity_calculendar_about.view.*
import kotlinx.android.synthetic.main.dialog_calculendar_daynight_mode.view.*
import kotlinx.android.synthetic.main.dialog_exclusion_options.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Dialogs {
    fun showToastPrompt(context: Context, message: String, length: Int) {
        Toast.makeText(context, message, length).show()
    }

    fun showAboutDialog(context: Context) {
        val aboutApplicationDialogView = LayoutInflater.from(context).inflate(R.layout.activity_calculendar_about, null)
        val developerProfiles = context.resources.getStringArray(R.array.developer_profiles)
        aboutApplicationDialogView.developers_list.setOnItemClickListener{ _, _, position, _ ->
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(developerProfiles[position]))
                context.startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("Calculendar", "Device does not have a browser available")
            }
        }

        aboutApplicationDialogView.version_text.text = context.resources.getString(R.string.build_id_formatter).format(Utilities.getPackageVersionName(context))

        val aboutDialog = AlertDialog.Builder(context)
            .setView(aboutApplicationDialogView)
            .setTitle("Calculendar Developers")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        aboutApplicationDialogView.developers_list.adapter = ArrayAdapter(context, R.layout.developer_name_list_item, context.resources.getStringArray(R.array.developer_names))
        aboutDialog.show()
    }

    fun showDeleteAllDialog(context: Context) {
        val deleteConfirmationDialog = AlertDialog.Builder(context)
        deleteConfirmationDialog.setTitle("Delete All Calculations")
        deleteConfirmationDialog.setMessage("Do you want to delete all stored calculations?")
        deleteConfirmationDialog.setPositiveButton("Yes") { dialog, _ ->
            GlobalScope.launch {
                AppDatabase.getInstance(context)?.deleteAll()
            }

            dialog.dismiss()
        }

        deleteConfirmationDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        deleteConfirmationDialog.create().show()
    }

    fun showDeleteConfirmationDialog(context: Context, calculationId: Int, isActivity: Boolean) {
        val deleteConfirmationDialog = AlertDialog.Builder(context)
        deleteConfirmationDialog.setTitle("Delete Calculation?")
        deleteConfirmationDialog.setMessage("Do you want to delete the current calculation?")
        deleteConfirmationDialog.setPositiveButton("Yes") { dialog, _ ->
            GlobalScope.launch {
                    AppDatabase.getInstance(context)?.deleteById(calculationId)
            }

            if (isActivity) {
                (context as Activity).onBackPressed()
            }

            dialog.dismiss()
        }

        deleteConfirmationDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        deleteConfirmationDialog.create().show()
    }

    fun showThemeDialog(context: Context, preferenceManager: PreferenceManager, currentTheme: Theme) {
        val themeDialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calculendar_daynight_mode, null)
        var preferredDayNightMode = currentTheme
        val autoModeButton = dialogView.findViewById<RadioButton>(R.id.radio_auto_mode)

        when (preferredDayNightMode) {
            Theme.Day -> dialogView.radio_day_mode.isChecked = true
            Theme.Night -> dialogView.radio_night_mode.isChecked = true
            Theme.PowerSave -> dialogView.radio_battery_mode.isChecked = true
            Theme.System -> autoModeButton.isChecked = true
        }

        dialogView.radio_day_mode.setOnCheckedChangeListener {
           _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Day }
        }

        dialogView.radio_night_mode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Night }
        }

        dialogView.radio_battery_mode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.PowerSave}
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            autoModeButton.visibility = View.VISIBLE

            dialogView.radio_auto_mode.setOnCheckedChangeListener {
                _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.System }
            }
        }

        themeDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            GlobalScope.launch {
                preferenceManager.setTheme(preferredDayNightMode)
            }
            dialog.dismiss()
        }

        themeDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        themeDialogBuilder.setTitle(R.string.theme_dialog_title)
        themeDialogBuilder.setView(dialogView)
        themeDialogBuilder.show()
    }

    fun showAnalyticsDialog(context: Context, preferenceManager: PreferenceManager) {
        val analyticsDialogBuilder = AlertDialog.Builder(context)

        analyticsDialogBuilder.setTitle(R.string.opt_in_dialog_title)
        analyticsDialogBuilder.setMessage(R.string.opt_in_dialog_message)

        analyticsDialogBuilder.setNegativeButton("Opt-Out") { dialog, _ ->
            GlobalScope.launch {
                preferenceManager.setAnalytics(false)
                dialog.dismiss()
            }
        }

        analyticsDialogBuilder.setPositiveButton("Opt-In") { dialog, _ ->
            GlobalScope.launch {
                preferenceManager.setAnalytics(true)
                dialog.dismiss()
            }
        }

        analyticsDialogBuilder.show()
    }

    fun showResultDialog(context: Context, result: String) {
        val resultDialogBuilder = AlertDialog.Builder(context)
        resultDialogBuilder.setTitle("Calculation Result")
        resultDialogBuilder.setMessage(result)

        resultDialogBuilder.setNeutralButton("OK") {dialog, _ ->
            dialog.dismiss()
        }

        resultDialogBuilder.create().show()
    }

    suspend fun showDatePickerDialog(context: Context, dialogTitle: String, rangeSelectionMode: Boolean, selectedDates: MutableList<Date> = mutableListOf(), excludedDates: MutableList<Date> = mutableListOf()) = suspendCoroutine<MutableList<Date>> {
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

    suspend fun showExclusionOptionsDialog(context: Context, dateRange: String, selectedDates: MutableList<Date>) = suspendCoroutine<Pair<MutableList<Date>, ExclusionMode>?> {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exclusion_options, null)
        var exclusionMode: ExclusionMode = ExclusionMode.None
        var excludedDates: MutableList<Date> = mutableListOf()

        dialogView.exclusionHeader.text = dateRange
        dialogView.exclusionOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val exclusionOptions = context.resources.getStringArray(R.array.exclusion_options)
            exclusionMode = when (exclusionOptions[position]) {
                "Exclude None" -> ExclusionMode.None
                "Exclude Saturdays" -> ExclusionMode.Saturdays
                "Exclude Sundays" -> ExclusionMode.Sundays
                "Exclude Both" -> ExclusionMode.Both
                "Exclude Custom" -> ExclusionMode.CustomDates
                else -> ExclusionMode.None
            }

            dialogView.btn_pick_custom.visibility = when (exclusionMode) {
                ExclusionMode.CustomDates -> View.VISIBLE
                else -> View.INVISIBLE
            }

            dialogView.btn_pick_custom.text = Converters.getFormattedCustomDateString(context, excludedDates.size)
        }

    }

        dialogView.btn_pick_custom.setOnClickListener() {
            GlobalScope.launch(Dispatchers.Main) {
                excludedDates =  showDatePickerDialog(context, context.resources.getString(R.string.custom_date_dialog_title),false, selectedDates, excludedDates)
                dialogView.btn_pick_custom.text = Converters.getFormattedCustomDateString(context, excludedDates.size)
            }
        }

        AlertDialog.Builder(context)
            .setTitle("Exclusion Options")
            .setView(dialogView)
            .setPositiveButton("Calculate") {
                dialog, _ ->
                it.resume(Pair(excludedDates, exclusionMode))
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {
                dialog, _ ->
                it.resume(null)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

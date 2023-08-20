package com.kennethfechter.calculendar.businesslogic

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.businesslogic.Utilities.getPackageInfo
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.databinding.ActivityCalculendarAboutBinding
import com.kennethfechter.calculendar.databinding.DialogCalculendarDaynightModeBinding
import com.kennethfechter.calculendar.databinding.DialogExclusionOptionsBinding
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import com.kennethfechter.calculendar.enumerations.Theme
import com.squareup.timessquare.CalendarPickerView
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Dialogs {
    fun showToastPrompt(context: Context, message: String, length: Int) {
        Toast.makeText(context, message, length).show()
    }

    fun showAboutDialog(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        val developerProfiles = context.resources.getStringArray(R.array.developer_profiles)
        val binding = ActivityCalculendarAboutBinding.inflate(layoutInflater)
        binding.developersList.setOnItemClickListener{ _, _, position, _ ->
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(developerProfiles[position]))
                context.startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("Calculendar", "Device does not have a browser available")
            }
        }
        val versionNameString = context.getPackageInfo().versionName
        binding.versionText.text = context.resources.getString(R.string.build_id_formatter).format(versionNameString)

        val aboutDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setTitle("Calculendar Developers")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        binding.developersList.adapter = ArrayAdapter(context, R.layout.developer_name_list_item, context.resources.getStringArray(R.array.developer_names))
        aboutDialog.show()
    }

    fun showDeleteAllDialog(context: Context) {
        val deleteConfirmationDialog = AlertDialog.Builder(context)
        deleteConfirmationDialog.setTitle("Delete All Calculations")
        deleteConfirmationDialog.setMessage("Do you want to delete all stored calculations?")
        deleteConfirmationDialog.setPositiveButton("Yes") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch{
                AppDatabase.getInstance(context)?.deleteAll()
            }
            dialog.dismiss()
        }

        deleteConfirmationDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        deleteConfirmationDialog.create().show()
    }

    @Suppress("DEPRECATION")
    fun showDeleteConfirmationDialog(context: Context, calculationId: Int, isActivity: Boolean) {
        val deleteConfirmationDialog = AlertDialog.Builder(context)
        deleteConfirmationDialog.setTitle("Delete Calculation?")
        deleteConfirmationDialog.setMessage("Do you want to delete the current calculation?")
        deleteConfirmationDialog.setPositiveButton("Yes") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
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

    fun showThemeDialog(context: Context, currentTheme: Theme) {
        val themeDialogBuilder = AlertDialog.Builder(context)
        val layoutInflater = LayoutInflater.from(context)
        val binding = DialogCalculendarDaynightModeBinding.inflate(layoutInflater)
        var preferredDayNightMode = currentTheme

        when (preferredDayNightMode) {
            Theme.Day -> binding.radioDayMode.isChecked = true
            Theme.Night -> binding.radioNightMode.isChecked = true
            Theme.PowerSave -> binding.radioBatteryMode.isChecked = true
            Theme.System -> binding.radioAutoMode.isChecked = true
        }

        binding.radioDayMode.setOnCheckedChangeListener {
           _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Day }
        }

        binding.radioNightMode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Night }
        }

        binding.radioBatteryMode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.PowerSave}
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.radioAutoMode.visibility = View.VISIBLE

            binding.radioAutoMode.setOnCheckedChangeListener {
                _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.System }
            }
        }

        themeDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                context.setAppTheme(preferredDayNightMode)
            }
            dialog.dismiss()
        }

        themeDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        themeDialogBuilder.setTitle(R.string.theme_dialog_title)
        themeDialogBuilder.setView(binding.root)
        themeDialogBuilder.show()
    }

    fun showAnalyticsDialog(context: Context) {
        val analyticsDialogBuilder = AlertDialog.Builder(context)

        analyticsDialogBuilder.setTitle(R.string.opt_in_dialog_title)
        analyticsDialogBuilder.setMessage(R.string.opt_in_dialog_message)

        analyticsDialogBuilder.setNegativeButton("Opt-Out") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                context.setAnalytics(false)
                dialog.dismiss()
            }
        }

        analyticsDialogBuilder.setPositiveButton("Opt-In") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                context.setAnalytics(true)
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

    fun showSpotLight(context: Context, parentActivity: CalculationListActivity, view: View) {
        BubbleShowCaseBuilder(parentActivity)
            .title("Long press to delete")
            .description("Long press the add (+) button to delete all calculations.")
            .listener(object : BubbleShowCaseListener { //Listener for user actions
                override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                    CoroutineScope(Dispatchers.IO).launch {
                        context.setTutorial(true)
                    }
                    bubbleShowCase.dismiss()
                }
                override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                    CoroutineScope(Dispatchers.IO).launch {
                        context.setTutorial(true)
                    }
                    bubbleShowCase.dismiss()
                }
                override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                    CoroutineScope(Dispatchers.IO).launch {
                        context.setTutorial(true)
                    }
                    bubbleShowCase.dismiss()
                }

                override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                    CoroutineScope(Dispatchers.IO).launch {
                        context.setTutorial(true)
                    }
                    bubbleShowCase.dismiss()
                }
            })
            .targetView(view)
            .show()
    }

    suspend fun showExclusionOptionsDialog(context: Context, dateRange: String, selectedDates: MutableList<Date>) = suspendCoroutine {
        val layoutInflater = LayoutInflater.from(context)
        val binding = DialogExclusionOptionsBinding.inflate(layoutInflater)
        var exclusionMode: ExclusionMode = ExclusionMode.None
        var excludedDates: MutableList<Date> = mutableListOf()

        binding.exclusionHeader.text = dateRange
        binding.exclusionOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

            binding.btnPickCustom.visibility = when (exclusionMode) {
                ExclusionMode.CustomDates -> View.VISIBLE
                else -> View.INVISIBLE
            }

            binding.btnPickCustom.text = Converters.getFormattedCustomDateString(context, excludedDates.size)
        }

    }

        binding.btnPickCustom.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(Dispatchers.Main) {
                excludedDates =  showDatePickerDialog(context, context.resources.getString(R.string.custom_date_dialog_title),false, selectedDates, excludedDates)
                binding.btnPickCustom.text = Converters.getFormattedCustomDateString(context, excludedDates.size)
            }
        }

        AlertDialog.Builder(context)
            .setTitle("Exclusion Options")
            .setView(binding.root)
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

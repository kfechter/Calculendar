package com.kennethfechter.calculendar

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kennethfechter.calculendar.activities.CalculendarAbout
import com.kennethfechter.calculendar.businesslogic.Utilities
import kotlinx.android.synthetic.main.activity_calculendar_main.*
import kotlinx.coroutines.*
import java.util.*

class CalculendarMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val layoutId: Int = R.layout.activity_calculendar_main

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private var selectedDates: MutableList<Date> = mutableListOf()
    private var excludedDates: MutableList<Date> = mutableListOf()
    private var exclusionOption: String = ""

    private var analyticsPreferenceKey = ""
    private var firstRunPreferenceKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(layoutId)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setSupportActionBar(toolbar)

            analyticsPreferenceKey = getString(R.string.preference_name_analytics_level)
            firstRunPreferenceKey = getString(R.string.first_run_preference_name)

            btn_pick_range.setOnClickListener {
                showRangeDialog()
            }

            btn_pick_custom.setOnClickListener {
                showCustomDialog()
            }

            btnPerformCalculation.setOnClickListener {
                val result = Utilities.calculateDays(this, selectedDates, excludedDates, exclusionOption)

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Calculation Result")
                dialogBuilder.setMessage(result)

                dialogBuilder.setNeutralButton("OK") {dialog, _ ->
                    dialog.dismiss()
                }

                dialogBuilder.create().show()
            }

            exclusionOptions.onItemSelectedListener = this

            val extraString = intent.extras?.getString("action")

            val firstRun = Utilities.retrieveBooleanSharedPref(this@CalculendarMain, firstRunPreferenceKey, true)
            if(extraString != null && extraString == "newCalc" && !firstRun) {
                showRangeDialog()
            }

            val currentPreferenceValue = Utilities.retrieveStringSharedPreference(this@CalculendarMain, analyticsPreferenceKey, "Not-Set")

            if(firstRun || currentPreferenceValue == "Not-Set") uiScope.launch {
                val analyticsLevel = Utilities.displayAnalyticsOptInDialog(this@CalculendarMain)
                setAnalytics(analyticsLevel)
            } else {
                setAnalytics(currentPreferenceValue)
            }

            val dayNightMode = Utilities.getDayNightMode(this@CalculendarMain)
            AppCompatDelegate.setDefaultNightMode(dayNightMode)
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculendar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_application -> navigateToAbout()

            R.id.analytics_opt_status -> uiScope.launch {
                val optStatus = Utilities.displayAnalyticsOptInDialog(this@CalculendarMain)
                setAnalytics(optStatus)
            }

            R.id.day_night_mode -> Utilities.showDayNightModeDialog(this@CalculendarMain)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModelJob.cancel()
    }

    override fun onStop() {
        super.onStop()
        viewModelJob.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelJob.cancel()
    }

    override fun onRestart() {
        super.onRestart()
        viewModelJob = Job()
    }

    override fun onResume() {
        super.onResume()
        viewModelJob = Job()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        val exclusionOptions = resources.getStringArray(R.array.exclusion_options)
        exclusionOption = exclusionOptions[position]
        when(exclusionOption) {
            "Exclude Custom" -> {
                btn_pick_custom.visibility = View.VISIBLE
                btn_pick_custom.text = Utilities.getCustomDatesFormatterString(this, excludedDates.size)
            }
            else -> btn_pick_custom.visibility = View.INVISIBLE
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    private fun navigateToAbout() {
        val aboutIntent = Intent(this, CalculendarAbout::class.java)
        startActivity(aboutIntent)
    }

    fun showRangeDialog() = uiScope.launch {
        val localSelectedDates = Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.date_picker_dialog_title),true)

        if(localSelectedDates.size > 1) {
            selectedDates = localSelectedDates
            exclusion_options_container.visibility = View.VISIBLE
            btnPerformCalculation.isEnabled = true
            btn_pick_range.text = Utilities.getSelectedRangeString(selectedDates)
        } else {
            Toast.makeText(this@CalculendarMain, "A valid date range was not selected", Toast.LENGTH_LONG).show()
        }
    }

    fun showCustomDialog() = uiScope.launch {
        excludedDates =  Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.custom_date_dialog_title),false, selectedDates, excludedDates)
        btn_pick_custom.text = Utilities.getCustomDatesFormatterString(this@CalculendarMain, excludedDates.size)
    }

    fun setAnalytics(analyticsLevel : String) {
        when(analyticsLevel) {
            getString(R.string.full_analytics_preference_value) -> {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                FirebaseAnalytics.getInstance(this@CalculendarMain).setAnalyticsCollectionEnabled(true)
            }

            getString(R.string.crash_only_analytics_preference_value) -> {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                FirebaseAnalytics.getInstance(this@CalculendarMain).setAnalyticsCollectionEnabled(false)
            }

            getString(R.string.no_analytics_preference_value) -> {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
                FirebaseAnalytics.getInstance(this@CalculendarMain).setAnalyticsCollectionEnabled(false)
            }
        }
    }
}
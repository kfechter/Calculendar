package com.kennethfechter.calculendar

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
import androidx.lifecycle.asLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kennethfechter.calculendar.businesslogic.*
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import com.kennethfechter.calculendar.enumerations.Theme
import kotlinx.android.synthetic.main.activity_calculendar_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.properties.Delegates

class CalculendarMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val layoutId: Int = R.layout.activity_calculendar_main

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private var selectedDates: MutableList<Date> = mutableListOf()
    private var excludedDates: MutableList<Date> = mutableListOf()
    private lateinit var exclusionOption: ExclusionMode


    private lateinit var currentTheme: Theme
    private var analyticsEnabled by Delegates.notNull<Int>()

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(layoutId)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // This will go away in the new UI
            setSupportActionBar(toolbar)

            preferenceManager = PreferenceManager(applicationContext)
            initializeAnalytics()
            initializeThemeObserver()

            btn_pick_range.setOnClickListener {
                showRangeDialog()
            }

            btn_pick_custom.setOnClickListener {
                showCustomDialog()
            }

            btnPerformCalculation.setOnClickListener {
                val result = DateCalculator.CalculateInterval(this, selectedDates, excludedDates, exclusionOption, false)
                Dialogs.showResultDialog(this, result)
            }

            exclusionOptions.onItemSelectedListener = this

            val extraString = intent.extras?.getString("action")

            if(extraString != null && extraString == "newCalc" && (analyticsEnabled != -1)) {
                showRangeDialog()
            }
        }

    fun initializeThemeObserver() {
        preferenceManager.themeFlow.asLiveData().observe(this) { theme ->
            when(theme) {
                Theme.Day -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Theme.Night -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Theme.PowerSave -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                null -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            currentTheme = theme
        }
    }

    fun initializeAnalytics() {
        preferenceManager.analyticsFlow.asLiveData().observe(this) { analyticsPreference ->
            when (analyticsPreference) {
                -1 -> if (!Utilities.isRunningTest) { Dialogs.showAnalyticsDialog(this@CalculendarMain, preferenceManager) }
                 0 -> if (!Utilities.isRunningTest) { configureAnalytics(false) }
                 1 -> if (!Utilities.isRunningTest) { configureAnalytics(true) }
            }

            analyticsEnabled = analyticsPreference
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculendar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_application -> Dialogs.showAboutDialog(this@CalculendarMain)
            R.id.analytics_opt_status -> Dialogs.showAnalyticsDialog(this@CalculendarMain, preferenceManager)
            R.id.day_night_mode -> Dialogs.showThemeDialog(this@CalculendarMain, preferenceManager, currentTheme)
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
        exclusionOption = when (exclusionOptions[position]) {
            "Exclude None" -> ExclusionMode.None
            "Exclude Saturdays" -> ExclusionMode.Saturdays
            "Exclude Sundays" -> ExclusionMode.Sundays
            "Exclude Both" -> ExclusionMode.Both
            "Exclude Custom" -> ExclusionMode.CustomDates
            else -> ExclusionMode.None
        }

        btn_pick_custom.visibility = when (exclusionOption) {
            ExclusionMode.CustomDates -> {
                View.VISIBLE
            }
            else ->  View.INVISIBLE
        }
        btn_pick_custom.text = Converters.getFormattedCustomDateString(this, excludedDates.size)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    fun showRangeDialog() = uiScope.launch {
        val localSelectedDates = Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.date_picker_dialog_title),true)

        if(localSelectedDates.size > 1) {
            selectedDates = localSelectedDates
            exclusion_options_container.visibility = View.VISIBLE
            btnPerformCalculation.isEnabled = true
            btn_pick_range.text = Converters.getSelectedRangeString(selectedDates)
        } else {
            Toast.makeText(this@CalculendarMain, "A valid date range was not selected", Toast.LENGTH_LONG).show()
        }
    }

    fun showCustomDialog() = uiScope.launch {
        excludedDates =  Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.custom_date_dialog_title),false, selectedDates, excludedDates)
        btn_pick_custom.text = Converters.getFormattedCustomDateString(this@CalculendarMain, excludedDates.size)
    }

    fun configureAnalytics(analyticsEnabled: Boolean) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(analyticsEnabled);
        FirebaseAnalytics.getInstance(this@CalculendarMain).setAnalyticsCollectionEnabled(analyticsEnabled)
    }
}
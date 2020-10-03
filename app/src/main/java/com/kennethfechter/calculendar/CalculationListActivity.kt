package com.kennethfechter.calculendar

import android.os.Bundle
import android.view.*
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kennethfechter.calculendar.businesslogic.DateCalculator
import com.kennethfechter.calculendar.businesslogic.Dialogs
import com.kennethfechter.calculendar.businesslogic.PreferenceManager
import com.kennethfechter.calculendar.businesslogic.Utilities
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.CalculationRecyclerViewAdapter

import com.kennethfechter.calculendar.enumerations.Theme
import kotlin.properties.Delegates

class CalculationListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    private lateinit var currentTheme: Theme
    private var analyticsEnabled by Delegates.notNull<Int>()

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculation_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        preferenceManager = PreferenceManager(applicationContext)
        initializeAnalytics()
        initializeThemeObserver()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            DateCalculator.startCalculation(this@CalculationListActivity)
        }

        if (findViewById<NestedScrollView>(R.id.calculation_detail_container) != null) {
            twoPane = true
            findViewById<FloatingActionButton>(R.id.fab).setOnLongClickListener {
                // Handle Deletion of selected item
                true
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.calculation_list)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        initializeCalculationList(recyclerView)

        val extraString = intent.extras?.getString("action")

        if (extraString != null && extraString == "newCalc" && (analyticsEnabled != -1)) {
            DateCalculator.startCalculation(this@CalculationListActivity)
        }
    }

    private fun initializeThemeObserver() {
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

    private fun initializeAnalytics() {
        preferenceManager.analyticsFlow.asLiveData().observe(this) { analyticsPreference ->
            when (analyticsPreference) {
                -1 -> if (!Utilities.isRunningTest) { Dialogs.showAnalyticsDialog(this@CalculationListActivity, preferenceManager) }
                0 -> if (!Utilities.isRunningTest) { configureAnalytics(false) }
                1 -> if (!Utilities.isRunningTest) { configureAnalytics(true) }
            }

            analyticsEnabled = analyticsPreference
        }
    }

    private fun configureAnalytics(analyticsEnabled: Boolean) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(analyticsEnabled)
        FirebaseAnalytics.getInstance(this@CalculationListActivity).setAnalyticsCollectionEnabled(analyticsEnabled)
    }

    private fun initializeCalculationList(recyclerView: RecyclerView) {
        AppDatabase.getInstance(this@CalculationListActivity)!!.getAll().observe(this) {
            calculationList -> recyclerView.adapter = CalculationRecyclerViewAdapter(this, calculationList, twoPane)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculendar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_application -> Dialogs.showAboutDialog(this@CalculationListActivity)
            R.id.analytics_opt_status -> Dialogs.showAnalyticsDialog(this@CalculationListActivity, preferenceManager)
            R.id.day_night_mode -> Dialogs.showThemeDialog(this@CalculationListActivity, preferenceManager, currentTheme)
        }

        return super.onOptionsItemSelected(item)
    }
}
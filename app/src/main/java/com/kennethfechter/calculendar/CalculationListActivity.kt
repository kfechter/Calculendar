package com.kennethfechter.calculendar

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kennethfechter.calculendar.businesslogic.DateCalculator
import com.kennethfechter.calculendar.businesslogic.Dialogs
import com.kennethfechter.calculendar.businesslogic.PreferenceManager
import com.kennethfechter.calculendar.businesslogic.Utilities

import com.kennethfechter.calculendar.dummy.DummyContent
import com.kennethfechter.calculendar.enumerations.Theme
import com.kennethfechter.calculendar.views.CalculationDetailActivity
import com.kennethfechter.calculendar.views.CalculationDetailFragment
import kotlin.properties.Delegates

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [CalculationDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
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
            DateCalculator.StartCalculation(this@CalculationListActivity)
        }

        if (findViewById<NestedScrollView>(R.id.calculation_detail_container) != null) {
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.calculation_list))

        val extraString = intent.extras?.getString("action")

        if (extraString != null && extraString == "newCalc" && (analyticsEnabled != -1)) {
            DateCalculator.StartCalculation(this@CalculationListActivity)
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: CalculationListActivity,
        private val values: List<DummyContent.DummyItem>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = CalculationDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(CalculationDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.calculation_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, CalculationDetailActivity::class.java).apply {
                        putExtra(CalculationDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.calculation_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
        }
    }
}
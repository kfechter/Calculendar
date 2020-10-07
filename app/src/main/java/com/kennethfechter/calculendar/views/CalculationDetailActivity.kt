package com.kennethfechter.calculendar.views

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.businesslogic.Dialogs
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * An activity representing a single Calculation detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [CalculationListActivity].
 */
class CalculationDetailActivity : AppCompatActivity() {

    private var calculationId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculation_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            Dialogs.showDeleteConfirmationDialog(this@CalculationDetailActivity, calculationId!!, true)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            val fragment = CalculationDetailFragment().apply {
                val intExtra = intent.getIntExtra(CalculationDetailFragment.ARG_ITEM_ID, 0)
                calculationId = intExtra
                arguments = Bundle().apply {
                    putInt(
                        CalculationDetailFragment.ARG_ITEM_ID,
                        intExtra
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.calculation_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, CalculationListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
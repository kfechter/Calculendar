package com.kennethfechter.calculendar3

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
import com.kennethfechter.calculendar3.activities.CalculendarAbout
import com.kennethfechter.calculendar3.businesslogic.Utilities
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

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(layoutId)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setSupportActionBar(toolbar)

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
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculendar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_application -> navigateToAbout()
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
        selectedDates = Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.date_picker_dialog_title),true)

        if(selectedDates.size > 1) {
            exclusion_options_container.visibility = View.VISIBLE
            btnPerformCalculation.isEnabled = true
        } else {
            Toast.makeText(this@CalculendarMain, "A date range has not been selected", Toast.LENGTH_LONG).show()
        }
    }

    fun showCustomDialog() = uiScope.launch {
        excludedDates =  Utilities.displayDatePickerDialog(this@CalculendarMain, resources.getString(R.string.custom_date_dialog_title),false, selectedDates, excludedDates)
    }
}
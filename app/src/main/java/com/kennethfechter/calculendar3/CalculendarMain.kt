package com.kennethfechter.calculendar3

import android.content.Intent
import android.content.pm.ActivityInfo
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kennethfechter.calculendar3.activities.CalculendarAbout
import com.kennethfechter.calculendar3.businesslogic.Utilities
import com.squareup.timessquare.CalendarPickerView
import kotlinx.android.synthetic.main.activity_calculendar_main.*
import java.util.*

class CalculendarMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val layoutId: Int = R.layout.activity_calculendar_main

    private var selectedDates: MutableList<Date> = mutableListOf()
    private var excludedDates: MutableList<Date> = mutableListOf()
    private var exclusionOption: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(layoutId)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setSupportActionBar(toolbar)

            btn_pick_range.setOnClickListener{
                showRangePickerDialog()
            }

            btn_pick_custom.setOnClickListener {
                showCustomDateSelectionDialog()
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

    private fun showRangePickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calculendar_datepicker, null)

        val calendarPicker: CalendarPickerView = dialogView.findViewById(R.id.calendar_view)
        val pastDate = Calendar.getInstance()
        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.YEAR, 1)
        pastDate.add(Calendar.YEAR, -1)
        val today = Date()

            calendarPicker.init(pastDate.time, futureDate.time)
                .inMode(CalendarPickerView.SelectionMode.RANGE)

        calendarPicker.selectDate(today, true)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(R.string.date_picker_dialog_title)

        dialogBuilder.setPositiveButton("Select"){_, _ -> }

        dialogBuilder.setNegativeButton("Cancel"){_, _ -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener{
                if(calendarPicker.selectedDates.size > 1) {
                    alertDialog.dismiss()
                    btn_pick_range.text = Utilities.getSelectedRangeString(calendarPicker.selectedDates)
                    selectedDates = calendarPicker.selectedDates
                    exclusion_options_container.visibility = View.VISIBLE
                } else {
                    Toast.makeText(applicationContext, "A date range has not been selected", Toast.LENGTH_LONG).show()
                }
            }

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener{
                alertDialog.dismiss()
            }
    }

    private fun showCustomDateSelectionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calculendar_datepicker, null)

        val calendarPicker: CalendarPickerView = dialogView.findViewById(R.id.calendar_view)
        if(selectedDates.size > 1) {
            calendarPicker.init(selectedDates[0], selectedDates[selectedDates.size - 1])
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(excludedDates)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.custom_date_dialog_title)

            dialogBuilder.setPositiveButton("Select"){_, _ -> }

            dialogBuilder.setNegativeButton("Cancel"){_, _ -> }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener{
                        alertDialog.dismiss()
                        excludedDates = calendarPicker.selectedDates
                        btn_pick_custom.text = Utilities.getCustomDatesFormatterString(this, excludedDates.size)
                }

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener{
                    alertDialog.dismiss()
                }
        }

    }
}

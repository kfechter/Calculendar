package com.kennethfechter.calculendar3.wizardfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kennethfechter.calculendar3.R
import com.squareup.timessquare.CalendarPickerView
import java.util.*
import kotlin.Exception

class DateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.date_fragment_layout, container, false)

        val calendarPicker: CalendarPickerView = view.findViewById(R.id.calendar_view)
        val pastDate = Calendar.getInstance()
        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.YEAR, 5)
        pastDate.add(Calendar.YEAR, -5)
        val today = Date()
        calendarPicker.init(pastDate.time, futureDate.time)
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withSelectedDate(today)
        activity?.title = "Choose Date Range"

        calendarPicker.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date?) {
                if (calendarPicker.selectedDates.size > 1)
                {
                    // date range is selected, send data to activity
                }
            }

            override fun onDateUnselected(date: Date?) {
                // don't do anything?
            }
        })
        return view
    }

    companion object {
        fun create() : DateFragment {
            return DateFragment()
        }
    }
}
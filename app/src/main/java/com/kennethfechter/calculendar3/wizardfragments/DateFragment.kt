package com.kennethfechter.calculendar3.wizardfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kennethfechter.calculendar3.R
import com.squareup.timessquare.CalendarPickerView
import java.util.*

class DateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.date_fragment_layout, container, false)

        val calendarPicker: CalendarPickerView = view.findViewById(R.id.calendar_view)
        val pastDate = Calendar.getInstance()
        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.YEAR, 5)
        pastDate.add(Calendar.YEAR, -5)
        val todaysDate = Date()
        calendarPicker.init(pastDate.time, futureDate.time)
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withSelectedDate(todaysDate)
        return view
    }

    companion object {
        fun create(title: String) : DateFragment {
            val dateFragment = DateFragment()
            val args = Bundle()
            args.putString("title", title)
            dateFragment.arguments = args
            return dateFragment
        }
    }
}
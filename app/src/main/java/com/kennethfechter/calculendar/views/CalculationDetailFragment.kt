package com.kennethfechter.calculendar.views

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.businesslogic.Converters
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.Calculation

/**
 * A fragment representing a single Calculation detail screen.
 * This fragment is either contained in a [com.kennethfechter.calculendar.CalculationListActivity]
 * in two-pane mode (on tablets) or a [CalculationDetailActivity]
 * on handsets.
 */
class CalculationDetailFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                initializeContentView(it.getInt(ARG_ITEM_ID))
            }
        }
    }

    private fun initializeContentView(id: Int) {
        if(id != -1) {
            AppDatabase.getInstance(context!!)?.getByID(id)!!.observeOnce(this) {
                    calculation -> loadViewCalculation(calculation)
            }
        }
        else {
            blankView()
        }
    }

    private fun blankView() {
        val blankText = ""
        activity?.findViewById<TextView>(R.id.calculation_detail)?.text = getString(R.string.no_calculation_prompt)
        activity?.findViewById<TextView>(R.id.exclusion_mode)?.text = blankText
        activity?.findViewById<TextView>(R.id.excluded_days)?.text = blankText
        activity?.findViewById<TextView>(R.id.calculated_interval)?.text = blankText
        activity?.findViewById<TextView>(R.id.custom_dates_header)?.visibility = View.INVISIBLE
        val listView: ListView? = activity?.findViewById(R.id.custom_dates_list)
        listView?.adapter = null
        listView?.visibility = View.INVISIBLE
    }

    private fun loadViewCalculation(calculation: Calculation) {
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.calculation_title)
        activity?.findViewById<TextView>(R.id.calculation_detail)?.text = getString(R.string.date_range_formatter).format(calculation.startDate, calculation.endDate)
        activity?.findViewById<TextView>(R.id.exclusion_mode)?.text = getString(R.string.exclusion_mode_format).format(calculation.exclusionMethod)
        activity?.findViewById<TextView>(R.id.excluded_days)?.text = getString(R.string.excluded_days_format).format(calculation.numExcludedDates)
        activity?.findViewById<TextView>(R.id.calculated_interval)?.text = getString(R.string.calculated_interval_format).format(calculation.calculatedInterval)

        val customDateHeader: TextView =  activity?.findViewById(R.id.custom_dates_header)!!

        if (calculation.exclusionMethod == "CustomDates" && calculation.numExcludedDates ?: 0 > 0) {
            customDateHeader.visibility = View.VISIBLE
            val listView: ListView = activity?.findViewById(R.id.custom_dates_list)!!
            listView.visibility = View.VISIBLE
            listView.adapter = ArrayAdapter(context!!, R.layout.developer_name_list_item, Converters.getCustomDateStringList(calculation.customDates)!!)
        }
        else {
            customDateHeader.text = getString(R.string.no_custom_date_text)
            customDateHeader.visibility = View.VISIBLE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calculation_detail, container, false)
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
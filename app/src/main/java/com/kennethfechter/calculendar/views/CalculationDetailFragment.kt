package com.kennethfechter.calculendar.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kennethfechter.calculendar.R
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
        AppDatabase.getInstance(context!!)?.getByID(id)!!.observe(this) {
            calculation -> loadViewCalculation(calculation)
        }
    }

    private fun loadViewCalculation(calculation: Calculation) {
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = getString(R.string.calculation_title)
        activity?.findViewById<TextView>(R.id.calculation_detail)?.text = getString(R.string.date_range_formatter).format(calculation.startDate, calculation.endDate)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calculation_detail, container, false)
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
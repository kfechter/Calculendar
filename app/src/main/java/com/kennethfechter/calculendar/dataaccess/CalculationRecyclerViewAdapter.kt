package com.kennethfechter.calculendar.dataaccess

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.views.CalculationDetailActivity
import com.kennethfechter.calculendar.views.CalculationDetailFragment

class CalculationRecyclerViewAdapter(
    private val parentActivity: CalculationListActivity,
    private val values: List<Calculation>,
    private val twoPane: Boolean) : RecyclerView.Adapter<CalculationRecyclerViewAdapter.ViewHolder>()
{

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener {v ->
            val item = v.tag as Calculation
            if (twoPane) {
                val fragment = CalculationDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt(CalculationDetailFragment.ARG_ITEM_ID, item.uid)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.calculation_detail_container, fragment)
                    .commit()
            }
            else {
                val intent = Intent(v.context, CalculationDetailActivity::class.java).apply {
                    putExtra(CalculationDetailFragment.ARG_ITEM_ID, item.uid)
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
        holder.idView.text = item.uid.toString()
        holder.startDateView.text = holder.itemView.context.getString(R.string.start_date_formatter).format(item.startDate)
        holder.endDateView.text = holder.itemView.context.getString(R.string.end_date_formatter).format(item.endDate)

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.id_text)
        val startDateView: TextView = view.findViewById(R.id.startDate)
        val endDateView: TextView = view.findViewById(R.id.endDate)

    }
}
package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.views.CalculationDetailFragment
import com.squareup.timessquare.CalendarPickerView
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Utilities {

    val isRunningTest : Boolean by lazy {
        try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    fun getPackageVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    fun blankFragment(parentActivity: CalculationListActivity) {
        val fragment = CalculationDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(CalculationDetailFragment.ARG_ITEM_ID, -1)
            }
        }
        parentActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.calculation_detail_container, fragment)
            .commit()
    }
}


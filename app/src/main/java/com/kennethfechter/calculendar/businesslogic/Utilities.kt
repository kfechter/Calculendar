package com.kennethfechter.calculendar.businesslogic

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.kennethfechter.calculendar.CalculationListActivity
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.views.CalculationDetailFragment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object Utilities {

    val isRunningTest : Boolean by lazy {
        try {
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    @Suppress("DEPRECATION")
    fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
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


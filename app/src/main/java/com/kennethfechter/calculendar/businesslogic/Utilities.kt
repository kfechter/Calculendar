package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import com.kennethfechter.calculendar.R
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
}


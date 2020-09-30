package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.kennethfechter.calculendar.R
import com.kennethfechter.calculendar.enumerations.Theme
import kotlinx.android.synthetic.main.activity_calculendar_about.view.*
import kotlinx.android.synthetic.main.dialog_calculendar_daynight_mode.view.*
import kotlinx.coroutines.*

object Dialogs {
    fun ShowToastPrompt(context: Context, message: String, length: Int) {
        Toast.makeText(context, message, length).show()
    }

    fun showAboutDialog(context: Context) {
        val aboutApplicationDialogView = LayoutInflater.from(context).inflate(R.layout.activity_calculendar_about, null)
        val developerProfiles = context.resources.getStringArray(R.array.developer_profiles)
        aboutApplicationDialogView.developers_list.setOnItemClickListener{ _, _, position, _ ->
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(developerProfiles[position]))
                context.startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("Calculendar", "Device does not have a browser available")
            }
        }

        val aboutDialog = AlertDialog.Builder(context)
            .setView(aboutApplicationDialogView)
            .setTitle(context.resources.getString(R.string.build_id_formatter).format(Utilities.getPackageVersionName(context)))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        aboutApplicationDialogView.developers_list.adapter = ArrayAdapter(context, R.layout.developer_name_list_item, context.resources.getStringArray(R.array.developer_names))
        aboutDialog.show()
    }

    fun showThemeDialog(context: Context, preferenceManager: PreferenceManager, currentTheme: Theme) {
        val themeDialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calculendar_daynight_mode, null)
        var preferredDayNightMode = currentTheme
        val autoModeButton = dialogView.findViewById<RadioButton>(R.id.radio_auto_mode)

        when (preferredDayNightMode) {
            Theme.Day -> dialogView.radio_day_mode.isChecked = true
            Theme.Night -> dialogView.radio_night_mode.isChecked = true
            Theme.PowerSave -> dialogView.radio_battery_mode.isChecked = true
            Theme.System -> autoModeButton.isChecked = true
        }

        dialogView.radio_day_mode.setOnCheckedChangeListener {
           _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Day }
        }

        dialogView.radio_night_mode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.Night }
        }

        dialogView.radio_battery_mode.setOnCheckedChangeListener {
            _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.PowerSave}
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            autoModeButton.visibility = View.VISIBLE

            dialogView.radio_auto_mode.setOnCheckedChangeListener {
                _, isChecked -> if (isChecked) { preferredDayNightMode = Theme.System }
            }
        }

        themeDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            GlobalScope.launch {
                preferenceManager.setTheme(preferredDayNightMode)
            }
            dialog.dismiss()
        }

        themeDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        themeDialogBuilder.setTitle(R.string.theme_dialog_title)
        themeDialogBuilder.setView(dialogView)
        themeDialogBuilder.show()
    }
}
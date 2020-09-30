package com.kennethfechter.calculendar.businesslogic

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kennethfechter.calculendar.R
import kotlinx.android.synthetic.main.activity_calculendar_about.*
import kotlinx.android.synthetic.main.activity_calculendar_about.view.*

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
}
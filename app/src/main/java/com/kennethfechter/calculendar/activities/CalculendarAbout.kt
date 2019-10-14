package com.kennethfechter.calculendar.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kennethfechter.calculendar.R

import kotlinx.android.synthetic.main.activity_calculendar_about.*
import android.content.Intent
import android.net.Uri
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import com.kennethfechter.calculendar.businesslogic.Utilities


class CalculendarAbout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculendar_about)
        setSupportActionBar(toolbar)
        versionIdFooter.text = resources.getString(R.string.build_id_formatter).format(Utilities.getPackageVersionName(this))
        val developerProfiles = resources.getStringArray(R.array.developer_profiles)
        developers_list.adapter = ArrayAdapter(baseContext, R.layout.developer_name_list_item, resources.getStringArray(R.array.developer_names))
        developers_list.setOnItemClickListener{ _, _, position, _ ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(developerProfiles[position]))
            startActivity(browserIntent)
        }
    }
}

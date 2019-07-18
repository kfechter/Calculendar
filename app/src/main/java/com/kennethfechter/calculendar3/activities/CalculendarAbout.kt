package com.kennethfechter.calculendar3.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kennethfechter.calculendar3.R

import kotlinx.android.synthetic.main.activity_calculendar_about.*
import android.content.Intent
import android.net.Uri


class CalculendarAbout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculendar_about)
        setSupportActionBar(toolbar)

        val developerProfiles = resources.getStringArray(R.array.developer_profiles)

        developers_list.setOnItemClickListener{ _, _, position, _ ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(developerProfiles[position]))
            startActivity(browserIntent)
        }
    }

}

package com.kennethfechter.calculendar3.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kennethfechter.calculendar3.R

import kotlinx.android.synthetic.main.activity_calculendar_about.*

class CalculendarAbout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculendar_about)
        setSupportActionBar(toolbar)
    }

}

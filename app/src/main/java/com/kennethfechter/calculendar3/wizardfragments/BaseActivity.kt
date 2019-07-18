package com.kennethfechter.calculendar3.wizardfragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.kennethfechter.calculendar3.R
import com.kennethfechter.calculendar3.activities.CalculendarAbout
import kotlinx.android.synthetic.main.base_view_layout.*


abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var viewPager: ViewPager2
    protected open val layoutId: Int = R.layout.base_view_layout

    private val pageAnimator = ViewPager2.PageTransformer { page, _ ->
        page.apply {
            rotation = 0f
            translationY = 0f
            translationX = 0f
            scaleX = 1f
            scaleY = 1f
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        viewPager = findViewById(R.id.view_pager)
        viewPager.setPageTransformer(pageAnimator)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calculendar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_application -> navigateToAbout()
        }
        return true
    }

    private fun navigateToAbout() {
        val intent = Intent(this, CalculendarAbout::class.java)
        startActivity(intent)
    }
}
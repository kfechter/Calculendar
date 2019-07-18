package com.kennethfechter.calculendar3.wizardfragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.kennethfechter.calculendar3.R


abstract class BaseActivity : FragmentActivity() {

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
    }
}
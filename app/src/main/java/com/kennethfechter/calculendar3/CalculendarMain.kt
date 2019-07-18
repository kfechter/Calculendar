package com.kennethfechter.calculendar3

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kennethfechter.calculendar3.wizardfragments.BaseActivity
import com.kennethfechter.calculendar3.wizardfragments.DateFragment

class CalculendarMain : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> DateFragment.create()
                    else -> DateFragment.create()
                }
            }

            override fun getItemCount(): Int {
                return 1
            }
        }
    }
}

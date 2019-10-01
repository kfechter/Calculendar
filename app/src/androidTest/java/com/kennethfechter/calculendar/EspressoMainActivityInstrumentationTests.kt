package com.kennethfechter.calculendar

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not

class EspressoMainActivityInstrumentationTests
{

    @get:Rule
    val activity = ActivityTestRule(CalculendarMain::class.java)

    @Test
    fun testShowDateDialog(){
        Espresso.onView(ViewMatchers.withId(R.id.btn_pick_range))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Select")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun testSelectInvalidRange() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_pick_range))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Select"))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("A date range has not been selected"))
            .inRoot(withDecorView(not(`is`(activity.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }
}
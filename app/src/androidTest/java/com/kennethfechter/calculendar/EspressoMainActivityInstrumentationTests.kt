package com.kennethfechter.calculendar

import android.app.Instrumentation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.kennethfechter.calculendar.activities.CalculendarAbout
import org.hamcrest.CoreMatchers
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
        Espresso.onView(withId(R.id.btn_pick_range))
            .perform(click())

        Espresso.onView(withText("Select")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun testSelectInvalidRange() {
        Espresso.onView(withId(R.id.btn_pick_range))
            .perform(click())

        Espresso.onView(withText("Select"))
            .perform(click())

        Espresso.onView(withText("A date range has not been selected"))
            .inRoot(withDecorView(not(`is`(activity.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAboutAppButton() {

        Intents.init()
        val expectedIntentkfechter = CoreMatchers.allOf(IntentMatchers.hasComponent(hasClassName(CalculendarAbout::class.java.name)))
        Intents.intending(expectedIntentkfechter).respondWith(Instrumentation.ActivityResult(0, null))
        Espresso.onView(withId(R.id.about_application)).perform(click())
        Intents.intended(expectedIntentkfechter)
        Intents.release()
    }
}
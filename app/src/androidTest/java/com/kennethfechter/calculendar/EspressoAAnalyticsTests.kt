package com.kennethfechter.calculendar;

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class EspressoAAnalyticsTests {
    @get:Rule
    val activity = ActivityTestRule(CalculendarMain::class.java)

    @Test
    fun testAnalyticsOptDialog() {
        val dialogText = activity.activity.getString(R.string.opt_in_dialog_message)
        onView(withText(dialogText)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText("Opt-Out"))
            .perform(ViewActions.click())
    }
}

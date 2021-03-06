package com.kennethfechter.calculendar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class EspressoAAnalyticsTests {
    @get:Rule
    val activity = ActivityTestRule(CalculendarMain::class.java)

    @Test
    fun testAnalyticsOptDialogFirstRun() {
        val dialogText = activity.activity.getString(R.string.opt_in_dialog_message)
        val context = activity.activity.applicationContext
        val preferenceKey = context.getString(R.string.first_run_preference_name)
        val performTest =  Utilities.retrieveBooleanSharedPref(context, preferenceKey, true)

        if(performTest) {
            onView(withText(dialogText)).inRoot(isDialog()).check(matches(isDisplayed()))

            onView(withText("Opt-Out"))
                .perform(click())
        }
    }
}

package com.kennethfechter.calculendar

import android.app.Instrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.kennethfechter.calculendar.activities.CalculendarAbout
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.hamcrest.CoreMatchers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


class EspressoMainActivityInstrumentationTests
{

    @get:Rule
    val activity = ActivityTestRule(CalculendarMain::class.java)

    lateinit var selectedDatesList: MutableList<Date>
    lateinit var customDatesList: MutableList<Date>

    @Before
    fun setup() {
        selectedDatesList = mutableListOf()
        customDatesList = mutableListOf()

        selectedDatesList.add(Date(1567310400000))
        selectedDatesList.add(Date(1567396800000))
        selectedDatesList.add(Date(1567483200000))
        selectedDatesList.add(Date(1567569600000))
        selectedDatesList.add(Date(1567656000000))
        selectedDatesList.add(Date(1567742400000))
        selectedDatesList.add(Date(1567828800000))
        selectedDatesList.add(Date(1567915200000))
        selectedDatesList.add(Date(1568001600000))
        selectedDatesList.add(Date(1568088000000))
        selectedDatesList.add(Date(1568174400000))
        selectedDatesList.add(Date(1568260800000))
        selectedDatesList.add(Date(1568347200000))
        selectedDatesList.add(Date(1568433600000))
        selectedDatesList.add(Date(1568520000000))
        selectedDatesList.add(Date(1568606400000))
        selectedDatesList.add(Date(1568692800000))
        selectedDatesList.add(Date(1568779200000))
        selectedDatesList.add(Date(1568865600000))
        selectedDatesList.add(Date(1568952000000))
        selectedDatesList.add(Date(1569038400000))
        selectedDatesList.add(Date(1569124800000))
        selectedDatesList.add(Date(1569211200000))
        selectedDatesList.add(Date(1569297600000))
        selectedDatesList.add(Date(1569384000000))
        selectedDatesList.add(Date(1569470400000))
        selectedDatesList.add(Date(1569556800000))
        selectedDatesList.add(Date(1569643200000))
        selectedDatesList.add(Date(1569729600000))
        selectedDatesList.add(Date(1569816000000))

        customDatesList.add(Date(1567396800000))
        customDatesList.add(Date(1567483200000))
        customDatesList.add(Date(1567569600000))
    }


    @Test
    fun testShowDateDialog(){
        onView(withId(R.id.btn_pick_range))
            .perform(click())

        onView(withText("Select")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun testSelectInvalidRange() {
        onView(withId(R.id.btn_pick_range))
            .perform(click())

        onView(withText("Select"))
            .perform(click())

        onView(withText("A valid date range was not selected"))
            .inRoot(withDecorView(not(`is`(activity.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCustomDateFormatting() {
        val multipleDatesString = Utilities.getCustomDatesFormatterString(activity.activity.applicationContext, 5)
        val noDatesString = Utilities.getCustomDatesFormatterString(activity.activity.applicationContext, 0)
        val singleDateString = Utilities.getCustomDatesFormatterString(activity.activity.applicationContext, 1)

        Assert.assertEquals("The formatted string does not match", "5 Custom Dates Selected", multipleDatesString)
        Assert.assertEquals("The formatted string does not match", "0 Custom Dates Selected", noDatesString)
        Assert.assertEquals("The formatted string does not match", "1 Custom Date Selected", singleDateString)
    }

    @Test
    fun testDateCalculation() {
        val context = activity.activity.applicationContext

        val exclusionOptions = arrayOf("Exclude None", "Exclude Saturdays", "Exclude Sundays", "Exclude Both", "Exclude Custom").iterator()
        val expectedOutcomes = mutableListOf<String>()

        expectedOutcomes.add("The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 0 exclusions is 30 Days")
        expectedOutcomes.add("The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 4 exclusions is 26 Days")
        expectedOutcomes.add("The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 5 exclusions is 25 Days")
        expectedOutcomes.add("The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 9 exclusions is 21 Days")
        expectedOutcomes.add("The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 3 exclusions is 27 Days")

        for ((index, value) in exclusionOptions.withIndex()) {
            val outcome = Utilities.calculateDays(context, selectedDates = selectedDatesList, customDateExclusions = customDatesList, exclusionMethod = value)
            Assert.assertEquals("The calculated result does not match: ", expectedOutcomes[index], outcome)
        }

    }

    @Test
    fun testAboutAppButton() {

        Intents.init()
        val expectedIntent = allOf(IntentMatchers.hasComponent(hasClassName(CalculendarAbout::class.java.name)))
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))
        onView(withId(R.id.about_application)).perform(click())
        intended(expectedIntent)
        Intents.release()
    }
}
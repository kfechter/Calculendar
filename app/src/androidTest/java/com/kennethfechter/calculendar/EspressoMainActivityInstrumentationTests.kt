package com.kennethfechter.calculendar

import android.app.Instrumentation
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.kennethfechter.calculendar.activities.CalculendarAbout
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.hamcrest.CoreMatchers.*
import org.junit.*
import java.util.*


class EspressoMainActivityInstrumentationTests {
    @get:Rule
    val activity = ActivityTestRule(CalculendarMain::class.java)

    private lateinit var selectedDatesList: MutableList<Date>
    private lateinit var customDatesList: MutableList<Date>

    private var testSharedPreferenceKey1 = "testSharedPreferenceKey1"
    private var testSharedPreferenceKey2 = "testSharedPreferenceKey2"

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

    @Test
    fun testAnalyticsOptDialog() {
        val context = activity.activity.applicationContext
        val dialogText = context.getString(R.string.opt_in_dialog_message)
        val fullButtonText = context.getString(R.string.dialog_button_full_analytics)
        val crashOnlyButtonText = context.getString(R.string.dialog_button_crash_only)
        val optOutButtonText = context.getString(R.string.dialog_button_opt_out)


        val preferenceKey = context.getString(R.string.preference_name_analytics_level)

        onView(withId(R.id.analytics_opt_status))
            .perform(click())

        /*
        *
        * Tests for checking that buttons are actually on the dialog
        *
        * */
        onView(withText(dialogText))
            .check(matches(isDisplayed()))

        onView(withText(fullButtonText))
            .check(matches(isDisplayed()))

        onView(withText(crashOnlyButtonText))
            .check(matches(isDisplayed()))

        onView(withText(optOutButtonText))
            .check(matches(isDisplayed()))

        /*
        *
        * Tests for testing dialog functionality
        *
        *
        * */

        onView(withText(fullButtonText))
            .perform(click())

        val preferenceValueFull = Utilities.retrieveStringSharedPreference(context, preferenceKey, "Not-Set")
        Assert.assertEquals("The analytics mode does not match", context.getString(R.string.full_analytics_preference_value), preferenceValueFull)


        /*
        *
        *
        * */

        onView(withId(R.id.analytics_opt_status))
            .perform(click())


        onView(withText(crashOnlyButtonText))
            .perform(click())

        val preferenceValueCrash = Utilities.retrieveStringSharedPreference(context, preferenceKey, "Not-Set")
        Assert.assertEquals("The analytics mode does not match", context.getString(R.string.crash_only_analytics_preference_value), preferenceValueCrash)

        /*
         *
         *
         * */

        onView(withId(R.id.analytics_opt_status))
            .perform(click())


        onView(withText(optOutButtonText))
            .perform(click())

        val preferenceValueNone = Utilities.retrieveStringSharedPreference(context, preferenceKey, "Not-Set")
        Assert.assertEquals("The analytics mode does not match", context.getString(R.string.no_analytics_preference_value), preferenceValueNone)

    }

    @Test
    fun testThemingButton() {
        val testAutoThemeMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        val dialogText = activity.activity.getString(R.string.theme_dialog_title)

        onView(withId(R.id.day_night_mode)).perform(click())
        onView(withText(dialogText)).check(matches(isDisplayed()))

        onView(withId(R.id.radio_day_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.radio_night_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.radio_battery_mode)).check(matches(isDisplayed()))

        onView(withId(R.id.radio_day_mode)).perform(click())
        onView(withText("OK")).perform(click())
        var currentDayNightMode = AppCompatDelegate.getDefaultNightMode()
        Assert.assertEquals("The Expected Mode does not match", AppCompatDelegate.MODE_NIGHT_NO, currentDayNightMode)


        onView(withId(R.id.day_night_mode)).perform(click())
        onView(withId(R.id.radio_night_mode)).perform(click())
        onView(withText("OK")).perform(click())

        currentDayNightMode = AppCompatDelegate.getDefaultNightMode()
        Assert.assertEquals("The Expected Mode does not match", AppCompatDelegate.MODE_NIGHT_YES, currentDayNightMode)


        onView(withId(R.id.day_night_mode)).perform(click())
        onView(withId(R.id.radio_battery_mode)).perform(click())
        onView(withText("OK")).perform(click())

        currentDayNightMode = AppCompatDelegate.getDefaultNightMode()
        Assert.assertEquals("The Expected Mode does not match", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, currentDayNightMode)

        if(testAutoThemeMode) {
            onView(withId(R.id.day_night_mode)).perform(click())
            onView(withId(R.id.radio_auto_mode)).check(matches(isDisplayed()))
            onView(withId(R.id.radio_auto_mode)).perform(click())
            onView(withText("OK")).perform(click())

            currentDayNightMode = AppCompatDelegate.getDefaultNightMode()
            Assert.assertEquals("The Expected Mode does not match", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, currentDayNightMode)
        }
    }

    @Test
    fun verifyDayNightMenuExists() {
        onView(withId(R.id.day_night_mode))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyAnalyticsMenuExists() {
        onView(withId(R.id.analytics_opt_status))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyAboutMenuExists() {
        onView(withId(R.id.about_application))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSharedPreferences() {

        Utilities.updateBooleanSharedPref(activity.activity.applicationContext,testSharedPreferenceKey1, true)
        Utilities.updateBooleanSharedPref(activity.activity.applicationContext, testSharedPreferenceKey2, false)

        val testSharedPref1 = Utilities.retrieveBooleanSharedPref(activity.activity.applicationContext, testSharedPreferenceKey1, false)
        val testSharedPref2 = Utilities.retrieveBooleanSharedPref(activity.activity.applicationContext, testSharedPreferenceKey2, true)

        Assert.assertEquals("The returned shared preference value does not match", true, testSharedPref1)
        Assert.assertEquals("The returned shared preference value does not match", false, testSharedPref2)
    }
}
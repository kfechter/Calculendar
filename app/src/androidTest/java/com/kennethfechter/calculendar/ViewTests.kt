package com.kennethfechter.calculendar

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.PreferenceManager
import com.kennethfechter.calculendar.businesslogic.Utilities
import com.kennethfechter.calculendar.dataaccess.Calculation
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ViewTests {

    @Rule @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activity = activityScenarioRule<CalculationListActivity>()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun verifyThemeDialogButton() {
        onView(withId(R.id.day_night_mode))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyAnalyticsButton() {
        onView(withId(R.id.analytics_opt_status))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyAboutApplicationButton() {
        onView(withId(R.id.about_application))
            .check(matches(isDisplayed()))
    }



    @Test
    fun verifyAboutDialog() {
        onView(withId(R.id.about_application)).perform(click())
        val versionCode = Utilities.getPackageVersionName(context)
        val dialogTitle = "Calculendar Developers"
        onView(withText(dialogTitle)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText("Kenneth Fechter"))
            .check(matches(isDisplayed()))
        onView(withText("Graham Klein"))
            .check(matches(isDisplayed()))
        onView(withText("Tobyn Collinsworth"))
            .check(matches(isDisplayed()))

        onView(withText("OK"))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(dialogTitle)).check(doesNotExist())
    }

    @Test
    fun verifyDialogIntent() {
        onView(withId(R.id.about_application)).perform(click())

        val kfechterLinkedInUrl = "https://www.linkedin.com/in/kafechter/"
        val gkleinLinkedInUrl = "https://www.linkedin.com/in/graham-klein-04b976106/"
        val tcollinLinkedInUrl = "https://www.linkedin.com/in/tobyncollinsworth/"

        Intents.init()

        val expectedIntentkfechter = allOf(hasAction(Intent.ACTION_VIEW), hasData(kfechterLinkedInUrl))
        intending(expectedIntentkfechter).respondWith(Instrumentation.ActivityResult(0, null))
        onView(withText("Kenneth Fechter")).perform(click())
        intended(expectedIntentkfechter)


        val expectedIntentgklein = allOf(hasAction(Intent.ACTION_VIEW), hasData(gkleinLinkedInUrl))
        intending(expectedIntentgklein).respondWith(Instrumentation.ActivityResult(0, null))
        onView(withText("Graham Klein")).perform(click())
        intended(expectedIntentgklein)


        val expectedIntenttcollin = allOf(hasAction(Intent.ACTION_VIEW), hasData(tcollinLinkedInUrl))
        intending(expectedIntenttcollin).respondWith(Instrumentation.ActivityResult(0, null))
        onView(withText("Tobyn Collinsworth")).perform(click())
        intended(expectedIntenttcollin)

        Intents.release()
    }

    @Test
    fun verifyThemeDialog() {
        val testAutoThemeMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        val dialogText = context.getString(R.string.theme_dialog_title)

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

        if (testAutoThemeMode) {
            onView(withId(R.id.day_night_mode)).perform(click())
            onView(withId(R.id.radio_auto_mode)).check(matches(isDisplayed()))
            onView(withId(R.id.radio_auto_mode)).perform(click())
            onView(withText("OK")).perform(click())

            currentDayNightMode = AppCompatDelegate.getDefaultNightMode()
            Assert.assertEquals("The Expected Mode does not match", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, currentDayNightMode)
        }

    }

    @Test
    fun verifyAnalyticsDialog() {
        val dialogText = context.getString(R.string.opt_in_dialog_message)

        val optInText = "Opt-In"
        val optOutText = "Opt-Out"

        onView(withId(R.id.analytics_opt_status))
            .perform(click())

        onView(withText(dialogText))
            .check(matches(isDisplayed()))

        onView(withText(optInText))
            .check(matches(isDisplayed()))

        onView(withText(optOutText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun validateOptOut() {
        val preferenceManager = PreferenceManager(context)

        onView(withId(R.id.analytics_opt_status))
            .perform(click())

        onView(withText("Opt-Out"))
            .perform(click())

        Assert.assertEquals("The current analytics value does not match the expected value", 0, getValue(preferenceManager.analyticsFlow.asLiveData()))
    }

    @Test
    fun validateOptIn() {
        val preferenceManager = PreferenceManager(context)

        onView(withId(R.id.analytics_opt_status))
            .perform(click())

        onView(withText("Opt-In"))
            .perform(click())

        Assert.assertEquals("The current analytics value does not match the expected value", 1, getValue(preferenceManager.analyticsFlow.asLiveData()))
    }

    // Copied from stackoverflow
    @Throws(InterruptedException::class)
    fun <Int> getValue(liveData: LiveData<Int>): Int {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<Int> {
            override fun onChanged(t: Int) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this) //To change body of created functions use File | Settings | File Templates.
            }

        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as Int
    }

}
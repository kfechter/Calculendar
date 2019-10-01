package com.kennethfechter.calculendar

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.rule.ActivityTestRule
import com.kennethfechter.calculendar.activities.CalculendarAbout
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.Espresso.onView
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.allOf


class EspressoAboutActivityInstrumentationTests
{
    @get:Rule
    val activity = ActivityTestRule(CalculendarAbout::class.java)


    @Test
    fun verifyDeveloperNames() {
        onView(withText("Kenneth Fechter"))
            .check(matches(isDisplayed()))
        onView(withText("Graham Klein"))
            .check(matches(isDisplayed()))
        onView(withText("Tobyn Collinsworth"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyDeveloperHeader() {
        onView(withText("Developers"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyAppVersionTitle(){
        onView(withText(containsString("Calculendar")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyDeveloperLinkedInAppLinks()
    {
        val kfechterLinkedInUrl = "https://www.linkedin.com/in/kafechter/"
        val gkleinLinkedInUrl = "https://www.linkedin.com/in/graham-klein-04b976106/"
        val tcollinLinkedInUrl = "https://www.linkedin.com/in/tobyncollinsworth/"

        Intents.init()

        val expectedIntentkfechter = allOf(hasAction(Intent.ACTION_VIEW), hasData(kfechterLinkedInUrl))
        intending(expectedIntentkfechter).respondWith(ActivityResult(0, null))
        onView(withText("Kenneth Fechter")).perform(click())
        intended(expectedIntentkfechter)


        val expectedIntentgklein = allOf(hasAction(Intent.ACTION_VIEW), hasData(gkleinLinkedInUrl))
        intending(expectedIntentgklein).respondWith(ActivityResult(0, null))
        onView(withText("Graham Klein")).perform(click())
        intended(expectedIntentgklein)


        val expectedIntenttcollin = allOf(hasAction(Intent.ACTION_VIEW), hasData(tcollinLinkedInUrl))
        intending(expectedIntenttcollin).respondWith(ActivityResult(0, null))
        onView(withText("Tobyn Collinsworth")).perform(click())
        intended(expectedIntenttcollin)

        Intents.release()
    }

}
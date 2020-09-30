package com.kennethfechter.calculendar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.Converters
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FunctionTests {

    private lateinit var context: Context

    @Rule
    @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testCustomStringFormatterSingular() {
        val singularDateString = Converters.getFormattedCustomDateString(context, 1)
        Assert.assertEquals("Formatted string does not match expected", "1 Custom Date Selected", singularDateString)
    }

    @Test
    fun testCustomStringFormatterMultiple() {
        val multipleDateString = Converters.getFormattedCustomDateString(context, 7)
        Assert.assertEquals("Formatted string does not match expected", "7 Custom Dates Selected", multipleDateString)
    }
}
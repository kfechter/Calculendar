package com.kennethfechter.calculendar

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.junit.Before
import org.junit.Test


class UtilityInstrumentationTests
{
    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun testGetSelectedRangeString() {

    }

    @Test
    fun testConvertDateToString() {

    }

    @Test
    fun testGetPackageVersionName() {

    }

    @Test
    fun testGetCustomDatesFormatterString() {

    }

    @Test
    fun testCalculateDays() {

    }

    @Test
    fun testDisplayDatePickerDialog() {

    }
}
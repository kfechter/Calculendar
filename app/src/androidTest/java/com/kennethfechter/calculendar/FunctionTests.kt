package com.kennethfechter.calculendar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.Converters
import com.kennethfechter.calculendar.businesslogic.DateCalculator
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class FunctionTests {

    private lateinit var context: Context

    @Rule
    @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var selectedDatesList: MutableList<Date>
    private lateinit var customDatesList: MutableList<Date>

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
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
    fun testCustomStringFormatterSingular() {
        val singularDateString = Converters.getFormattedCustomDateString(context, 1)
        Assert.assertEquals("Formatted string does not match expected", "1 Custom Date Selected", singularDateString)
    }

    @Test
    fun testCustomStringFormatterMultiple() {
        val multipleDateString = Converters.getFormattedCustomDateString(context, 7)
        Assert.assertEquals("Formatted string does not match expected", "7 Custom Dates Selected", multipleDateString)
    }

    @Test
    fun testDateCalculator() {

        val noExclusionResult = DateCalculator.calculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.None, false)
        val saturdayExclusionResult = DateCalculator.calculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.Saturdays, false)
        val sundayExclusionResult = DateCalculator.calculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.Sundays, false)
        val saturdaySundayExclusionResult = DateCalculator.calculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.Both, false)
        val customDateExclusionResult = DateCalculator.calculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.CustomDates, false)

        Assert.assertEquals("The returned value does not match", "The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 0 exclusions is 30 Days", noExclusionResult)
        Assert.assertEquals("The returned value does not match", "The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 4 exclusions is 26 Days", saturdayExclusionResult)
        Assert.assertEquals("The returned value does not match", "The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 5 exclusions is 25 Days", sundayExclusionResult)
        Assert.assertEquals("The returned value does not match", "The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 9 exclusions is 21 Days", saturdaySundayExclusionResult)
        Assert.assertEquals("The returned value does not match", "The interval from Sunday Sep 1, 2019 to Monday Sep 30, 2019 with 3 exclusions is 27 Days", customDateExclusionResult)
    }
}
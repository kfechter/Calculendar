package com.kennethfechter.calculendar

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.*


class UtilityInstrumentationTests
{
    lateinit var instrumentationContext: Context
    lateinit var selectedDatesList: MutableList<Date>

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        selectedDatesList = mutableListOf()
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
    }

    @Test
    fun testGetSelectedRangeString() {
        val rangeStringPassing = Utilities.getSelectedRangeString(selectedDatesList)
        val rangeStringFailing = Utilities.getSelectedRangeString(mutableListOf())

        assertEquals("The converted range string does not match:","Sunday Sep 1, 2019 - Monday Sep 30, 2019", rangeStringPassing)
        assertEquals("The converted range string does not match:","Invalid Range", rangeStringFailing)
    }

    @Test
    fun testConvertDateToString() {
        val convertedDateBeginning = Utilities.convertDateToString(selectedDatesList[0])
        val convertedDateEnd  = Utilities.convertDateToString(selectedDatesList[selectedDatesList.size - 1])

        assertEquals("The converted date string does not match:", "Sunday Sep 1, 2019", convertedDateBeginning)
        assertEquals("The converted date string does not match:", "Monday Sep 30, 2019", convertedDateEnd)
    }
}
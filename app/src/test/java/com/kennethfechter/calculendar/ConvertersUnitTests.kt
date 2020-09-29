package com.kennethfechter.calculendar

import com.kennethfechter.calculendar.businesslogic.Converters
import com.kennethfechter.calculendar.businesslogic.Utilities
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class ConvertersUnitTests {
    @Test
    fun dateToStringConversionTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1992)
        calendar.set(Calendar.MONTH, 7)
        calendar.set(Calendar.DAY_OF_MONTH, 23)

        val selectedDate = Converters.convertDateToString(calendar.time)
        assertEquals("Date string representation does not match expected value", "Sunday Aug 23, 1992", selectedDate)
    }

    @Test
    fun invalidDateRangeStringConversionTest() {
        assertEquals("Range string representation does not match expected value", "Invalid Range", Converters.getSelectedRangeString(mutableListOf()))
    }

    @Test
    fun validDateRangeStringConversionTest() {
        val selectedDatesList: MutableList<Date> = mutableListOf()
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

        val rangeStringPassing = Converters.getSelectedRangeString(selectedDatesList)
        assertEquals("The converted range string does not match:","Sunday Sep 1, 2019 - Monday Sep 30, 2019", rangeStringPassing)
    }
}
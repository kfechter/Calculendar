package com.kennethfechter.calculendar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.businesslogic.DateCalculator
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.Calculation
import com.kennethfechter.calculendar.dataaccess.CalculationDao
import com.kennethfechter.calculendar.enumerations.ExclusionMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class DatabaseTests {
    private var calculationDao: CalculationDao? = null
    private lateinit var context: Context
    private lateinit var selectedDatesList: MutableList<Date>
    private lateinit var customDatesList: MutableList<Date>

    @Rule @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        AppDatabase.TEST_MODE = true
        calculationDao = AppDatabase.getInstance(context)

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

    @After
    fun teardown() {
        calculationDao?.deleteAll()
    }

    @Test
    fun insertCalculation() = runBlocking {
        val calculation = Calculation(
            1,
            startDate = "Sunday Aug 23, 1992",
            endDate = "Sunday Aug 30, 1992",
            exclusionMethod = "None",
            customDates = "",
            numExcludedDates = 0,
            calculatedInterval = 7
        )

        calculationDao?.insert(calculation)
        val testCalculation = getValue(calculationDao?.getByID(calculation.uid)!!)

        Assert.assertEquals(
            "Returned item uid does not match",
            calculation.uid,
            testCalculation.uid
        )
        Assert.assertEquals(
            "Returned item startDate does not match",
            calculation.startDate,
            testCalculation.startDate
        )
        Assert.assertEquals(
            "Returned item endDate does not match",
            calculation.endDate,
            testCalculation.endDate
        )
        Assert.assertEquals(
            "Returned item exclusion method does not match",
            calculation.exclusionMethod,
            testCalculation.exclusionMethod
        )
        Assert.assertEquals(
            "Returned item customDates does not match",
            calculation.customDates,
            testCalculation.customDates
        )
        Assert.assertEquals(
            "Returned item numExcludedDates does not match",
            calculation.numExcludedDates,
            testCalculation.numExcludedDates
        )
        Assert.assertEquals(
            "Returned item calculatedInterval does not match",
            calculation.calculatedInterval,
            testCalculation.calculatedInterval
        )
    }

    @Test
    fun testCalculationStorage() = runBlocking {
        DateCalculator.CalculateInterval(context, selectedDatesList, customDatesList, ExclusionMode.Both, true)
        val testCalculation = getValue(calculationDao?.getByStartDate("Sunday Sep 1, 2019")!!)

        Assert.assertEquals(
            "Returned item startDate does not match",
            "Sunday Sep 1, 2019",
            testCalculation.startDate
        )
        Assert.assertEquals(
            "Returned item endDate does not match",
            "Monday Sep 30, 2019",
            testCalculation.endDate
        )
        Assert.assertEquals(
            "Returned item exclusion method does not match",
            "Both",
            testCalculation.exclusionMethod
        )
        Assert.assertEquals(
            "Returned item customDates does not match",
            null,
            testCalculation.customDates
        )
        Assert.assertEquals(
            "Returned item numExcludedDates does not match",
           9,
            testCalculation.numExcludedDates
        )
        Assert.assertEquals(
            "Returned item calculatedInterval does not match",
            21,
            testCalculation.calculatedInterval
        )
    }

    @Test
    fun deleteCalculation() {
        calculationDao?.deleteAll()
        Assert.assertEquals("Items were returned when none were expected", calculationDao?.getCalculationCount(), 0)
    }

    // Copied from stackoverflow
    @Throws(InterruptedException::class)
    fun <Calculation> getValue(liveData: LiveData<Calculation>): Calculation {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<Calculation> {
            override fun onChanged(t: Calculation) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)//To change body of created functions use File | Settings | File Templates.
            }

        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as Calculation
    }
}
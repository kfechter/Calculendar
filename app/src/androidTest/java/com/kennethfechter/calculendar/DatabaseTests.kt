package com.kennethfechter.calculendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import com.kennethfechter.calculendar.dataaccess.AppDatabase
import com.kennethfechter.calculendar.dataaccess.Calculation
import com.kennethfechter.calculendar.dataaccess.CalculationDao
import org.junit.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class DatabaseTests {
    private var calculationDao: CalculationDao? = null

    @Rule @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        AppDatabase.TEST_MODE = true
        calculationDao = AppDatabase.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun teardown() {

    }

    @Test
    fun insertCalculation() {
        val calculation = Calculation(
            1,
            calculatedOn = "Tuesday Sep 29, 2020",
            startDate = "Sunday Aug 23, 1992",
            endDate = "Sunday Aug 30, 1992",
            exclusionMethod = "None",
            customDates = "0",
            calculatedInterval = 7
        )

        calculationDao?.insertAll(calculation)
        val testCalculation = getValue(calculationDao?.getByID(calculation.uid)!!)

        Assert.assertEquals(
            "Returned item uid does not match",
            calculation.uid,
            testCalculation.uid
        )
        Assert.assertEquals(
            "Returned item calculatedOn does not match",
            calculation.calculatedOn,
            testCalculation.calculatedOn
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
            "Returned item calculatedInterval does not match",
            calculation.calculatedInterval,
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
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T?) {
                data[0] = t
                latch.countDown()
                liveData.removeObserver(this)//To change body of created functions use File | Settings | File Templates.
            }

        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as T
    }
}
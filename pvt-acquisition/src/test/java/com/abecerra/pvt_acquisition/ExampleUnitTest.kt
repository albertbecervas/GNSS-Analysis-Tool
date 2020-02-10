package com.abecerra.pvt_acquisition

import com.abecerra.pvt_acquisition.app.utils.Logger
import com.abecerra.pvt_computation.data.input.PvtInputData
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Logger.savePvtInputData("test.txt", PvtInputData())

        assertEquals(4, 2 + 2)
    }
}

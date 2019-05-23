package com.abecerra.pvt

import com.abecerra.pvt.computation.PVTEngine
import com.abecerra.pvt.computation.data.LlaLocation
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PVTEngineTest {
    @Test
    fun testPVTEngine() {
//        val fileInput = this.javaClass.classLoader?.getResourceAsStream("tests_jardi_1/jardi_1_${1}.txt")
//        val acqInfoString = fileInput?.bufferedReader().use { it?.readText() }
//        print(acqInfoString)

        PVTEngine().startComputing(LlaLocation())
    }

}

package com.abecerra.pvt

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val fileInput = this.javaClass.classLoader?.getResourceAsStream("tests_jardi_1/jardi_1_${1}.txt")
        val acqInfoString = fileInput?.bufferedReader().use { it?.readText() }
        print(acqInfoString)
    }
}

package com.abecerra.pvt_computation.domain.computation.filter

import com.abecerra.pvt_computation.data.input.PvtInputData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MaskFilteringTest {

    private lateinit var pvtInputData: PvtInputData

    @Before
    fun setUp(){
        pvtInputData = PvtInputData()
    }

    @Test
    fun testPvtDoesNotContainAnyMeasureThatShouldHaveBeenFiltered(){
        val filteredPvtInputData = MaskFiltering.filter(pvtInputData)

    }

}
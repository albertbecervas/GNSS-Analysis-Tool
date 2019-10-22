package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtAlgorithm
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class PvtComputationInteractorImplTest {

    @Mock
    lateinit var pvtAlgorithm: PvtAlgorithm

    lateinit var pvtComputationInteractor: PvtComputationInteractor

    @Before
    fun setUp() {
        pvtAlgorithm = Mockito.mock(PvtAlgorithm::class.java)
        pvtComputationInteractor = PvtComputationInteractorImpl(pvtAlgorithm)
    }

    @Test
    fun testComputePositionsReturnsEmptyListWhenDataIsNotValid() {
        val pvtInputData = PvtInputData()
        val computedPositions = pvtComputationInteractor.computePosition(pvtInputData)
        print(computedPositions)
        assert(computedPositions.isEmpty())
    }

    @Test
    fun testComputePositionsReturnsNotEmptyListWhenDataIsValid() {
        val pvtInputData = PvtInputData()
        val computedPositions = pvtComputationInteractor.computePosition(pvtInputData)
        print(computedPositions)
        assert(computedPositions.isNotEmpty())
    }

    @Test
    fun testComputePositionsErrorIsNotGreaterThanDefined() {
        //outliers
    }
}

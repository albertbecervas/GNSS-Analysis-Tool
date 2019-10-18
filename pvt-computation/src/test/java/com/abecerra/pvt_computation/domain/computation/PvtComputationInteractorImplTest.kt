package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputationAlgorithm
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class PvtComputationInteractorImplTest {

    @Mock
    lateinit var pvtComputationAlgorithm: PvtComputationAlgorithm

    lateinit var pvtComputationInteractor: PvtComputationInteractor

    @Before
    fun setUp() {
        pvtComputationAlgorithm = Mockito.mock(PvtComputationAlgorithm::class.java)
        pvtComputationInteractor = PvtComputationInteractorImpl(pvtComputationAlgorithm)
    }

    @Test
    fun computePositionReturnsNotNullListOfPositions() {
        val pvtInputData = PvtInputData()
        val computedPositions = pvtComputationInteractor.computePosition(pvtInputData)
        print(computedPositions)
        assert(computedPositions != null)
    }
}

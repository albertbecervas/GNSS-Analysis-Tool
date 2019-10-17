package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputationAlgorithm
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class PvtComputationInteractorImplTest {

    @Mock
    lateinit var pvtComputationAlgorithm: PvtComputationAlgorithm

    private var pvtComputationInteractor: PvtComputationInteractor? = null


    @Before
    fun setUp() {
        pvtComputationInteractor = PvtComputationInteractorImpl(pvtComputationAlgorithm)
    }

    @Test
    fun computePositionReturnsNonEmptyListOfPositions() {
        val pvtInputData = PvtInputData()
        val computedPositions = pvtComputationInteractor?.computePosition(pvtInputData)
        computedPositions?.let {
            assert(it.isNotEmpty())
        }
    }
}
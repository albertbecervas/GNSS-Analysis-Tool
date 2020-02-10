package com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares

import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.output.PvtFix
import com.abecerra.pvt_computation.data.output.PvtLatLng
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.mock.LeastSquaresInputDataMock
import org.junit.Before
import org.junit.Test

class LeastSquaresAlgorithmImplTest {

    private lateinit var leastSquaresAlgorithm: LeastSquaresAlgorithm

    private lateinit var leastSquaresInputData: LeastSquaresInputData

    @Before
    fun setUp() {
        leastSquaresAlgorithm = LeastSquaresAlgorithmImpl()
        leastSquaresInputData = LeastSquaresInputDataMock().leastSquaresInputData
    }

    @Test
    fun testInitLeastSquaresData() {

    }

    @Test
    fun testExecuteLeastSquares() {
        leastSquaresAlgorithm.computeLeastSquares(leastSquaresInputData)?.let {
            val expectedOutputPvt = PvtAlgorithmOutputData(
                PvtFix(
                    pvtLatLng = PvtLatLng(
                        41.50288588150539,
                        2.1201167300518198,
                        -1033.5083610992879,
                        -852.6126367786659
                    )
                )
            )

            assert(expectedOutputPvt.pvtFix == it.pvtFix)
        }
    }
}
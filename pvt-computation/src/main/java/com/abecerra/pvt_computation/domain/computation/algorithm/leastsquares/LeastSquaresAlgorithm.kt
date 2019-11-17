package com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.input.Epoch

interface LeastSquaresAlgorithm {

    fun initLsInputDataForConstellation(
        epoch: Epoch, constellation: Int, bands: List<Int>
    ): LeastSquaresInputData

    fun prepareLsInputData(
        referencePosition: EcefLocation, epoch: Epoch, pvtAlgorithmInputData: PvtAlgorithmInputData,
        pvtComputationData: LeastSquaresInputData
    ): LeastSquaresInputData

    fun computeLeastSquares(leastSquaresData: LeastSquaresInputData): PvtAlgorithmOutputData?

}

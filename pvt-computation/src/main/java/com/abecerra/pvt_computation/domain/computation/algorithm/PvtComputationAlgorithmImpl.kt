package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.Constants.GALILEO
import com.abecerra.pvt_computation.data.Constants.GPS
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputation.initLeastSquaresInputDataForConstellation
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputation.prepareLeastSquaresInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.leastSquares

class PvtComputationAlgorithmImpl : PvtComputationAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData):
            PvtAlgorithmOutputData? {

        val pvtAlgorithmOutputData: PvtAlgorithmOutputData? = null

        val computedPvtOutputs = arrayListOf<PvtAlgorithmOutputData>()

        algorithmInputData.epochMeasurements.forEach {
            computeEpoch(it, algorithmInputData)?.let { pvtAlgorithmOutputData ->
                computedPvtOutputs.add(pvtAlgorithmOutputData)
            }
        }

        return pvtAlgorithmOutputData
    }


    private fun computeEpoch(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData
    ): PvtAlgorithmOutputData? {
        return when {
            algorithmInputData.isGpsSelected() -> {
                computeForSingleConstellation(epoch, algorithmInputData, GPS)
            }
            algorithmInputData.isGalileoOnlySelected() -> {
                computeForSingleConstellation(epoch, algorithmInputData, GALILEO)
            }
            algorithmInputData.isMultiConstellationSelected() -> {
                computeForMultiConstellation(epoch, algorithmInputData)
            }
            else -> null
        }
    }

    private fun computeForSingleConstellation(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData, const: Int
    ): PvtAlgorithmOutputData? {

        var pvtAlgorithmOutputData: PvtAlgorithmOutputData? = null

        var leastSquaresInputData = initLeastSquaresInputDataForConstellation(epoch, const)

        repeat(Constants.PVT_ITER) {

            leastSquaresInputData = prepareLeastSquaresInputData(
                algorithmInputData.referenceLocation.ecefLocation,
                epoch, algorithmInputData, leastSquaresInputData
            )

            pvtAlgorithmOutputData = leastSquares(leastSquaresInputData)
        }

        return pvtAlgorithmOutputData
    }


    private fun computeForMultiConstellation(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData
    ): PvtAlgorithmOutputData? {

        var pvtAlgorithmOutputData: PvtAlgorithmOutputData? = null

        var gpsLeastSquaresInputData = initLeastSquaresInputDataForConstellation(epoch, GPS)
        var galLeastSquaresInputData = initLeastSquaresInputDataForConstellation(epoch, GALILEO)

        repeat(Constants.PVT_ITER) {

            gpsLeastSquaresInputData = prepareLeastSquaresInputData(
                algorithmInputData.referenceLocation.ecefLocation, epoch, algorithmInputData,
                gpsLeastSquaresInputData
            )

            galLeastSquaresInputData = prepareLeastSquaresInputData(
                algorithmInputData.referenceLocation.ecefLocation, epoch, algorithmInputData,
                galLeastSquaresInputData
            )

            val leastSquaresInputData = LeastSquaresInputData()
            leastSquaresInputData.p.addAll(gpsLeastSquaresInputData.p + galLeastSquaresInputData.p)
            leastSquaresInputData.a.addAll(galLeastSquaresInputData.a + galLeastSquaresInputData.a)

            pvtAlgorithmOutputData = leastSquares(leastSquaresInputData)
        }

        return pvtAlgorithmOutputData
    }
}

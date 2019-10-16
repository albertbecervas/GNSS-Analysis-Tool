package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmComputationInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.output.PvtEcef
import com.abecerra.pvt_computation.domain.computation.algorithm.gal.GalComputation
import com.abecerra.pvt_computation.domain.computation.algorithm.gal.GalComputation.computeGal
import com.abecerra.pvt_computation.domain.computation.algorithm.gps.GpsComputation
import com.abecerra.pvt_computation.domain.computation.algorithm.gps.GpsComputation.computeGps
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.leastSquares

class PvtComputationAlgorithmImpl : PvtComputationAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtFix {

        val pvtFix = PvtFix()

        val obtainedPvtFixes = arrayListOf<PvtFix>()

        algorithmInputData.epochMeasurements.forEach {

            computeEpoch(it, algorithmInputData)

        }

        return pvtFix
    }


    private fun computeEpoch(epoch: Epoch, algorithmInputData: PvtAlgorithmInputData) {
        var position = algorithmInputData.referenceLocation.ecefLocation

        var gpsPvtComputationData = GpsComputation.initGpsPvtComputationDataIfSelected(epoch)

        var galPvtComputatiomData = GalComputation.initGalPvtComputationDataIfSelected(epoch)

        var pvtAlgorithmComputationInputData: PvtAlgorithmComputationInputData =
            gpsPvtComputationData

        repeat(Constants.PVT_ITER) {

            when {
                algorithmInputData.isGpsSelected() -> {
                    pvtAlgorithmComputationInputData =
                        computeGps(position, epoch, algorithmInputData, gpsPvtComputationData)
                }
                algorithmInputData.isGalileoSelected() -> {
                    pvtAlgorithmComputationInputData =
                        computeGal(position, epoch, algorithmInputData, galPvtComputatiomData)

                }
            }

            if (algorithmInputData.isMultiConstellationSelected()){

            }

            computePvtWithLeastSquares(
                position,
                pvtAlgorithmComputationInputData,
                algorithmInputData
            )

        }
    }

    private fun computePvtWithLeastSquares(
        position: EcefLocation,
        pvtAlgorithmComputationInputData: PvtAlgorithmComputationInputData,
        pvtAlgorithmInputData: PvtAlgorithmInputData
    ) {
        leastSquares(
            position,
            pvtAlgorithmComputationInputData,
            pvtAlgorithmInputData.isMultiConstellationSelected(),
            pvtAlgorithmInputData.isWeightedLeastSquaresSelected()
        )
    }
}

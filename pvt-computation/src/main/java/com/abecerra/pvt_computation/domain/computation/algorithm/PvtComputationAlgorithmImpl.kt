package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmComputationInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputation.computePvtAlgorithmComputationInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.leastSquares

class PvtComputationAlgorithmImpl : PvtComputationAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtFix {

        val pvtFix = PvtFix()

        val obtainedPvtFixes = arrayListOf<PvtFix>()

        algorithmInputData.epochMeasurements.forEach {

            computeEpoch(it, algorithmInputData)?.let {
                obtainedPvtFixes.add(it)
            }

        }

        // TODO compute epoch mean

        return pvtFix
    }


    private fun computeEpoch(epoch: Epoch, algorithmInputData: PvtAlgorithmInputData): PvtFix? {
        var epochPvtFix: PvtFix? = PvtFix(algorithmInputData.referenceLocation)

        var gpsPvtComputationData =
            PvtComputation.initPvtComputationDataForConstellation(epoch, Constants.GPS)

        var galPvtComputationData =
            PvtComputation.initPvtComputationDataForConstellation(epoch, Constants.GALILEO)


        var pvtAlgorithmComputationInputData: PvtAlgorithmComputationInputData

        repeat(Constants.PVT_ITER) {


            epochPvtFix?.let {fix ->
                pvtAlgorithmComputationInputData = when {
                    algorithmInputData.isGpsSelected() -> {
                        gpsPvtComputationData =
                            computePvtAlgorithmComputationInputData(
                                fix.location.ecefLocation,
                                epoch, algorithmInputData, gpsPvtComputationData
                            )
                        gpsPvtComputationData
                    }
                    algorithmInputData.isGalileoSelected() -> {
                        galPvtComputationData =
                            computePvtAlgorithmComputationInputData(
                                fix.location.ecefLocation,
                                epoch, algorithmInputData, galPvtComputationData
                            )
                        galPvtComputationData
                    }
                    else -> gpsPvtComputationData
                }

                if (algorithmInputData.isMultiConstellationSelected()) {
                    pvtAlgorithmComputationInputData.p.clear()
                    pvtAlgorithmComputationInputData.p.addAll(
                        gpsPvtComputationData.p + galPvtComputationData.p
                    )

                    pvtAlgorithmComputationInputData.a.clear()
                    pvtAlgorithmComputationInputData.a.addAll(
                        gpsPvtComputationData.a + galPvtComputationData.a
                    )

                    //TODO: add
//                val multiConstcn0 = gpsCleanCn0 + galCleanCn0
                }

                val pvtAlgorithmComputationOutputData = leastSquares(
                    fix.location.ecefLocation,
                    pvtAlgorithmComputationInputData,
                    algorithmInputData.isMultiConstellationSelected(),
                    algorithmInputData.isWeightedLeastSquaresSelected()
                )

                pvtAlgorithmComputationOutputData?.pvtFix?.let {
                    epochPvtFix = it
                }?.let {
                    epochPvtFix = null
                }
            }
        }
        return epochPvtFix
    }
}

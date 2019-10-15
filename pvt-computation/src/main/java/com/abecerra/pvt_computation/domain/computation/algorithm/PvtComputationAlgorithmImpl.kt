package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.domain.computation.utils.leastSquares
import com.abecerra.pvt_computation.domain.computation.utils.outliers
import com.abecerra.pvt_computation.domain.corrections.getCtrlCorr
import com.abecerra.pvt_computation.domain.corrections.getPropCorr
import kotlin.math.pow
import kotlin.math.sqrt

class PvtComputationAlgorithmImpl : PvtComputationAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtFix {

        //First: We initialize the PvtFix object, which will be returned as the output of
        //the pvt algorithm.
        val pvtFix = PvtFix()

        //Here we will save results in order to compute a mean.
        val obtainedPvtFixes = arrayListOf<PvtFix>()

        algorithmInputData.epochMeasurements.forEach {

            computeEpoch(it, algorithmInputData)

        }

        return PvtFix()
    }


    fun computeEpoch(epoch: Epoch, algorithmInputData: PvtAlgorithmInputData) {
        var position = algorithmInputData.referenceLocation.ecefLocation

        val gpsPvtComputationData = initGpsPvtComputationDataIfSelected(epoch, algorithmInputData)

        repeat(Constants.PVT_ITER) { pvtIteration ->
            when {
                algorithmInputData.isGpsSelected() -> {
                    computeGps(position, epoch, algorithmInputData, gpsPvtComputationData)
                }
                algorithmInputData.isGalileoSelected() -> {

                }
            }
        }
    }

    fun initGpsPvtComputationDataIfSelected(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData
    ): GpsPvtComputationData? {
        return if (algorithmInputData.isGpsSelected()) {
            val gpsPvtComputationData = GpsPvtComputationData()
            with(gpsPvtComputationData) {
                gpsSatellites.addAll(epoch.getGpsSatelliteMeasurements())

                gpsSatellites.forEach {
                    gpsPr.add(it.pR)
                    gpsSvn.add(it.svid)
                    gpsCn0.add(it.cn0)
                }

                if (gpsSatellites.isNotEmpty()) {
                    val ctrlCorr =
                        getCtrlCorr(gpsSatellites[INITIAL_INDEX], epoch.tow, gpsPr[INITIAL_INDEX])
                    gpsX.add(ctrlCorr.ecefLocation)
                    gpsTcorr.add(ctrlCorr.tCorr)
                }
            }
            return gpsPvtComputationData
        } else null
    }

    fun computeGps(
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData,
        gpsPvtComputationData: GpsPvtComputationData?
    ): EcefLocation {

        gpsPvtComputationData?.run {

            gpsSatellites.forEachIndexed { j, gpsSat ->

                gpsCorr = Constants.C * gpsTcorr[j]

                //Propagation corrections
                val propCorr =
                    getPropCorr(
                        gpsX[j],
                        referencePosition,
                        epoch.ionoProto,
                        epoch.tow,
                        pvtAlgorithmInputData.computationSettings.corrections
                    )
                gpsCorr = gpsCorr - propCorr.tropoCorr - propCorr.ionoCorr


                gpsPrC = gpsPr[j] + gpsCorr

                //gps GeometricMatrix
                if (gpsPrC != 0.0) {
                    gpsD0 = sqrt(
                        (gpsX[j].x - referencePosition.x).pow(2) +
                                (gpsX[j].y - referencePosition.y).pow(2) +
                                (gpsX[j].z - referencePosition.z).pow(2)
                    )

                    gpsP.add(j, gpsPrC - gpsD0)

                    gpsAx = -(gpsX[j].x - referencePosition.x) / gpsD0
                    gpsAy = -(gpsX[j].y - referencePosition.y) / gpsD0
                    gpsAz = -(gpsX[j].z - referencePosition.z) / gpsD0

                    val row = arrayListOf(gpsAx, gpsAy, gpsAz, 1.0)
                    if (pvtAlgorithmInputData.isMultiConstellationSelected()) row.add(0.0)
                    gpsA.add(row)

                }

            }


            //pseudorange "RAIM"
            val cleanSatsInd = outliers(gpsP)
            cleanSatsInd.forEach {
                gpsP.removeAt(it)
                gpsA.removeAt(it)
                gpsCn0.removeAt(it)
            }


            //Least Squares
            try {
//                if (pvtAlgorithmInputData.isMultiConstellationSelected()) {
//                    val multiConstP = gpsP + galP
//                    val multiconstA = gpsA + galA
//                    val multiConstcn0 = gpsCleanCn0 + galCleanCn0
//
//                    responsePvtMultiConst =
//                        leastSquares(
//                            referencePosition,
//                            multiConstP,
//                            multiconstA,
//                            isMultiConst,
//                            multiConstcn0,
//                            isWeight
//                        )
//                } else {
//                    when {
//                        mode.constellations.contains(GPS) -> {
//                            responsePvtMultiConst =
//                                leastSquares(
//                                    position,
//                                    gpsP,
//                                    gpsA,
//                                    isMultiConst,
//                                    gpsCleanCn0,
//                                    isWeight
//                                )
//                        }
//                        mode.constellations.contains(GALILEO) -> {
//                            responsePvtMultiConst =
//                                leastSquares(
//                                    position,
//                                    galP,
//                                    galA,
//                                    isMultiConst,
//                                    galCleanCn0,
//                                    isWeight
//                                )
//                        }
//                    }
//                }
            } catch (e: Exception) {
                print("LS ERROR:::${e.localizedMessage}")
            }
        }

        return EcefLocation()
    }

    data class GpsPvtComputationData(
        val gpsA: ArrayList<ArrayList<Double>> = arrayListOf(),
        val gpsP: ArrayList<Double> = arrayListOf(),
        val gpsTcorr: ArrayList<Double> = arrayListOf(),
        var gpsPcorr: ArrayList<Double> = arrayListOf(),
        val gpsX: ArrayList<EcefLocation> = arrayListOf(),
        val gpsPr: ArrayList<Double> = arrayListOf<Double>(),
        val gpsSvn: ArrayList<Int> = arrayListOf<Int>(),
        val gpsCn0: ArrayList<Double> = arrayListOf<Double>(),
        val gpsSatellites: ArrayList<SatelliteMeasurements> = arrayListOf<SatelliteMeasurements>(),
        var gpsCorr: Double = 0.0,
        var gpsPrC: Double = 0.0,
        var gpsD0: Double = 0.0,
        var gpsAx: Double = 0.0,
        var gpsAy: Double = 0.0,
        var gpsAz: Double = 0.0
    )

    companion object {
        const val INITIAL_INDEX: Int = 0
    }
}

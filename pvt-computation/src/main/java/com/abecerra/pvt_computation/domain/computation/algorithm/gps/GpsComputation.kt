package com.abecerra.pvt_computation.domain.computation.algorithm.gps

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.utils.outliers
import com.abecerra.pvt_computation.domain.corrections.getCtrlCorr
import com.abecerra.pvt_computation.domain.corrections.getPropCorr
import kotlin.math.pow
import kotlin.math.sqrt

object GpsComputation {

    private const val INITIAL_INDEX: Int = 0

    fun initGpsPvtComputationDataIfSelected(epoch: Epoch): GpsPvtComputationData {
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
                    getCtrlCorr(
                        gpsSatellites[INITIAL_INDEX],
                        epoch.tow,
                        gpsPr[INITIAL_INDEX]
                    )
                gpsX.add(ctrlCorr.ecefLocation)
                gpsTcorr.add(ctrlCorr.tCorr)
            }
        }
        return gpsPvtComputationData
    }

    fun computeGps(
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData,
        gpsPvtComputationData: GpsPvtComputationData
    ): GpsPvtComputationData {

        gpsPvtComputationData.run {

            gpsSatellites.forEachIndexed { j, gpsSat ->

                gpsCorr =
                    getGpsPropagationCorrections(j, referencePosition, epoch, pvtAlgorithmInputData)

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
        }
        return gpsPvtComputationData
    }

    private fun GpsPvtComputationData.getGpsPropagationCorrections(
        j: Int,
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData
    ): Double {
        gpsCorr = Constants.C * gpsTcorr[j]
        val propCorr = getPropCorr(
            gpsX[j], referencePosition, epoch.ionoProto, epoch.tow,
            pvtAlgorithmInputData.computationSettings.corrections
        )
        return gpsCorr - propCorr.tropoCorr - propCorr.ionoCorr
    }


}

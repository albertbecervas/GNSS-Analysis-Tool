package com.abecerra.pvt_computation.domain.computation.algorithm.gal

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.utils.outliers
import com.abecerra.pvt_computation.domain.corrections.getCtrlCorr
import com.abecerra.pvt_computation.domain.corrections.getPropCorr
import kotlin.math.pow
import kotlin.math.sqrt

object GalComputation {

    private const val INITIAL_INDEX: Int = 0

    fun initGalPvtComputationDataIfSelected(epoch: Epoch): GalPvtComputationData {
        val galPvtComputationData = GalPvtComputationData()
        with(galPvtComputationData) {
            galSatellites.addAll(epoch.getGalSatelliteMeasurements())

            galSatellites.forEach {
                galPr.add(it.pR)
                galSvn.add(it.svid)
                galCn0.add(it.cn0)
            }

            if (galSatellites.isNotEmpty()) {
                val ctrlCorr =
                    getCtrlCorr(
                        galSatellites[INITIAL_INDEX],
                        epoch.tow,
                        galPr[INITIAL_INDEX]
                    )
                galX.add(ctrlCorr.ecefLocation)
                galTcorr.add(ctrlCorr.tCorr)
            }
        }
        return galPvtComputationData
    }

    fun computeGal(
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData,
        galPvtComputationData: GalPvtComputationData
    ): GalPvtComputationData {

        galPvtComputationData.run {

            galSatellites.forEachIndexed { j, galSat ->

                galCorr =
                    getGalPropagationCorrections(j, referencePosition, epoch, pvtAlgorithmInputData)

                galPrC = galPr[j] + galCorr

                //gal GeometricMatrix
                if (galPrC != 0.0) {
                    galD0 = sqrt(
                        (galX[j].x - referencePosition.x).pow(2) +
                                (galX[j].y - referencePosition.y).pow(2) +
                                (galX[j].z - referencePosition.z).pow(2)
                    )

                    galP.add(j, galPrC - galD0)

                    galAx = -(galX[j].x - referencePosition.x) / galD0
                    galAy = -(galX[j].y - referencePosition.y) / galD0
                    galAz = -(galX[j].z - referencePosition.z) / galD0

                    val row = arrayListOf(galAx, galAy, galAz, 1.0)
                    if (pvtAlgorithmInputData.isMultiConstellationSelected()) row.add(0.0)
                    galA.add(row)
                }
            }

            //pseudorange "RAIM"
            val cleanSatsInd = outliers(galP)
            cleanSatsInd.forEach {
                galP.removeAt(it)
                galA.removeAt(it)
                galCn0.removeAt(it)
            }
        }
        return galPvtComputationData
    }

    private fun GalPvtComputationData.getGalPropagationCorrections(
        j: Int,
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData
    ): Double {
        galCorr = Constants.C * galTcorr[j]
        val propCorr = getPropCorr(
            galX[j], referencePosition, epoch.ionoProto, epoch.tow,
            pvtAlgorithmInputData.computationSettings.corrections
        )
        return galCorr - propCorr.tropoCorr - propCorr.ionoCorr
    }


}

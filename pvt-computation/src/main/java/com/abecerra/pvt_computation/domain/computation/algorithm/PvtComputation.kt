package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.utils.outliers
import com.abecerra.pvt_computation.domain.computation.corrections.getCtrlCorr
import com.abecerra.pvt_computation.domain.computation.corrections.getPropCorr
import kotlin.math.pow
import kotlin.math.sqrt

object PvtComputation {

    private const val INITIAL_INDEX: Int = 0

    fun initLeastSquaresInputDataForConstellation(
        epoch: Epoch,
        constellation: Int
    ): LeastSquaresInputData {
        val pvtAlgorithmComputationInputData = LeastSquaresInputData()
        with(pvtAlgorithmComputationInputData) {
            satellites.addAll(epoch.getConstellationSatellites(constellation))

            satellites.forEachIndexed { index, sat ->
                pR.add(sat.pR)
                svn.add(sat.svid)
                cn0.add(sat.cn0)
                val ctrlCorr = getCtrlCorr(sat, epoch.tow, sat.pR)
                x.add(ctrlCorr.ecefLocation)
                tCorr.add(ctrlCorr.tCorr)
            }
        }
        return pvtAlgorithmComputationInputData
    }

    fun prepareLeastSquaresInputData(
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData,
        pvtComputationData: LeastSquaresInputData
    ): LeastSquaresInputData {

        pvtComputationData.run {

            refPosition = referencePosition
            isMultiC = pvtAlgorithmInputData.isMultiConstellationSelected()
            isWeight = pvtAlgorithmInputData.isWeightedLeastSquaresSelected()
            a.clear()
            p.clear()

            satellites.forEachIndexed { j, Sat ->

                corr =
                    getPropagationCorrections(j, referencePosition, epoch, pvtAlgorithmInputData)

                pRc = pR[j] + corr

                // GeometricMatrix
                if (pRc != 0.0) {
                    d0 = sqrt(
                        (x[j].x - referencePosition.x).pow(2) +
                                (x[j].y - referencePosition.y).pow(2) +
                                (x[j].z - referencePosition.z).pow(2)
                    )

                    p.add(j, pRc - d0)

                    aX = -(x[j].x - referencePosition.x) / d0
                    aY = -(x[j].y - referencePosition.y) / d0
                    aZ = -(x[j].z - referencePosition.z) / d0

                    val row = arrayListOf(aX, aY, aZ, 1.0)
                    if (pvtAlgorithmInputData.isMultiConstellationSelected()) row.add(0.0)
                    a.add(row)
                }
            }

            //pseudorange "RAIM"
            val cleanSatsInd = outliers(p)
            cleanSatsInd.forEach {
                p.removeAt(it)
                a.removeAt(it)
                cn0.removeAt(it)
            }
        }
        return pvtComputationData
    }

    private fun LeastSquaresInputData.getPropagationCorrections(
        j: Int,
        referencePosition: EcefLocation,
        epoch: Epoch,
        pvtAlgorithmInputData: PvtAlgorithmInputData
    ): Double {
        corr = Constants.C * tCorr[j]
        val propCorr = getPropCorr(
            x[j], referencePosition, epoch.ionoProto, epoch.tow,
            pvtAlgorithmInputData.computationSettings.corrections
        )
        return corr - propCorr.tropoCorr - propCorr.ionoCorr
    }
}

package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.PvtConstants.GALILEO
import com.abecerra.pvt_computation.data.PvtConstants.GPS
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.output.*
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputation.initLeastSquaresInputDataForConstellation
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputation.prepareLeastSquaresInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.leastSquares

class PvtAlgorithmImpl : PvtAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData):
            PvtAlgorithmOutputData? {

        val computedPvtOutputs = arrayListOf<PvtAlgorithmOutputData>()

        algorithmInputData.epochMeasurements.forEach {
            computeEpoch(it, algorithmInputData)?.let { pvtAlgorithmOutputData ->
                computedPvtOutputs.add(pvtAlgorithmOutputData)
            }
        }

        computedPvtOutputs.forEach {
            println("${it.pvtFix.pvtLatLng}")
        }

        return getPvtAlgorithmOutputsMean(computedPvtOutputs)
    }

    private fun getPvtAlgorithmOutputsMean(
        computedPvtOutputs: ArrayList<PvtAlgorithmOutputData>
    ): PvtAlgorithmOutputData? {

        val pvtLatLng = PvtLatLng()
        val dop = Dop()
        var residue = 0.0
        var nSats = 0f
        val corrections = Corrections()
        computedPvtOutputs.forEach { algorithmOutput ->
            with(algorithmOutput) {
                pvtLatLng.lat += pvtFix.pvtLatLng.lat
                pvtLatLng.lng += pvtFix.pvtLatLng.lng
                pvtLatLng.altitude += pvtFix.pvtLatLng.altitude
                pvtLatLng.clockBias += pvtFix.pvtLatLng.clockBias

                dop.gDop += this.dop.gDop
                dop.pDop += this.dop.pDop
                dop.tDop += this.dop.tDop

                residue += this.residue

                nSats += nSatellites
            }
        }

        val computedPvtOutputsSize = computedPvtOutputs.size

        pvtLatLng.lat = pvtLatLng.lat / computedPvtOutputsSize
        pvtLatLng.lng = pvtLatLng.lng / computedPvtOutputsSize
        pvtLatLng.altitude = pvtLatLng.altitude / computedPvtOutputsSize
        pvtLatLng.clockBias = pvtLatLng.clockBias / computedPvtOutputsSize

        dop.gDop = dop.gDop / computedPvtOutputsSize
        dop.pDop = dop.pDop / computedPvtOutputsSize
        dop.tDop = dop.tDop / computedPvtOutputsSize

        residue /= computedPvtOutputsSize

        nSats /= computedPvtOutputsSize

        val gpsTime = if (computedPvtOutputs.isNotEmpty()) {
            computedPvtOutputs.last().gpsTime
        } else GpsTime(0)

        return PvtAlgorithmOutputData(
            pvtFix = PvtFix(pvtLatLng),
            dop = dop,
            residue = residue,
            corrections = corrections,
            nSatellites = nSats,
            gpsTime = gpsTime
        )
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

        repeat(PvtConstants.PVT_ITER) {

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

        repeat(PvtConstants.PVT_ITER) {

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

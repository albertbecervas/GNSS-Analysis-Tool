package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.PvtConstants.GALILEO
import com.abecerra.pvt_computation.data.PvtConstants.GPS
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.output.*
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.LeastSquaresAlgorithm

class PvtAlgorithmImpl(private val lsAlgorithm: LeastSquaresAlgorithm) : PvtAlgorithm {

    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData):
            PvtAlgorithmOutputData? {

        val computedPvtOutputs = arrayListOf<PvtAlgorithmOutputData>()

        algorithmInputData.epochMeasurements.forEach {
            computeEpoch(it, algorithmInputData)?.let { pvtAlgorithmOutputData ->
                computedPvtOutputs.add(pvtAlgorithmOutputData)
            }
        }

        return getPvtAlgorithmOutputsMean(computedPvtOutputs)
    }

    private fun computeEpoch(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData
    ): PvtAlgorithmOutputData? {
        return when {
            algorithmInputData.isGpsSelected() -> {
                computeEpochForSingleConstellation(epoch, algorithmInputData, GPS)
            }
            algorithmInputData.isGalileoOnlySelected() -> {
                computeEpochForSingleConstellation(epoch, algorithmInputData, GALILEO)
            }
            algorithmInputData.isMultiConstellationSelected() -> {
                computeEpochForMultiConstellation(epoch, algorithmInputData)
            }
            else -> null
        }
    }

    private fun computeEpochForSingleConstellation(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData, const: Int
    ): PvtAlgorithmOutputData? {

        var pvtAlgorithmOutputData: PvtAlgorithmOutputData? = null

        var leastSquaresInputData = lsAlgorithm.initLsInputDataForConstellation(epoch, const,
            algorithmInputData.computationSettings.bands)

        repeat(PvtConstants.PVT_ITER) {

            leastSquaresInputData = lsAlgorithm.prepareLsInputData(
                algorithmInputData.referenceLocation.ecefLocation,
                epoch, algorithmInputData, leastSquaresInputData
            )

            pvtAlgorithmOutputData = lsAlgorithm.computeLeastSquares(leastSquaresInputData)
        }

        return pvtAlgorithmOutputData
    }


    private fun computeEpochForMultiConstellation(
        epoch: Epoch, algorithmInputData: PvtAlgorithmInputData
    ): PvtAlgorithmOutputData? {

        var pvtAlgorithmOutputData: PvtAlgorithmOutputData? = null

        val bands = algorithmInputData.computationSettings.bands

        var gpsLeastSquaresInputData = lsAlgorithm.initLsInputDataForConstellation(epoch, GPS,
            bands)
        var galLeastSquaresInputData = lsAlgorithm.initLsInputDataForConstellation(epoch, GALILEO, bands)

        repeat(PvtConstants.PVT_ITER) {

            gpsLeastSquaresInputData = lsAlgorithm.prepareLsInputData(
                algorithmInputData.referenceLocation.ecefLocation, epoch, algorithmInputData,
                gpsLeastSquaresInputData
            )

            galLeastSquaresInputData = lsAlgorithm.prepareLsInputData(
                algorithmInputData.referenceLocation.ecefLocation, epoch, algorithmInputData,
                galLeastSquaresInputData
            )

            val leastSquaresInputData = LeastSquaresInputData()

            leastSquaresInputData.refPosition = algorithmInputData.referenceLocation.ecefLocation

            leastSquaresInputData.p.addAll(gpsLeastSquaresInputData.p)
            leastSquaresInputData.p.addAll(galLeastSquaresInputData.p)

            leastSquaresInputData.a.addAll(gpsLeastSquaresInputData.a)
            leastSquaresInputData.a.addAll(galLeastSquaresInputData.a)

            leastSquaresInputData.cn0.addAll(gpsLeastSquaresInputData.cn0)
            leastSquaresInputData.cn0.addAll(galLeastSquaresInputData.cn0)

            pvtAlgorithmOutputData = lsAlgorithm.computeLeastSquares(leastSquaresInputData)
        }

        return pvtAlgorithmOutputData
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

}

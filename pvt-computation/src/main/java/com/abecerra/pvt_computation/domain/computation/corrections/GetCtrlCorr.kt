package com.abecerra.pvt_computation.domain.computation.corrections

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.domain.computation.utils.satPos
import com.abecerra.pvt_computation.data.PvtConstants.C
import com.abecerra.pvt_computation.domain.computation.utils.checkTime
import com.abecerra.pvt_computation.domain.computation.utils.earthRotCorr
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import kotlin.math.pow

fun getCtrlCorr(
    satellite: SatelliteMeasurements,
    tow: Double,
    pR: Double
): CtrlCorr {

    // Compute transmission time
    val txRaw = tow - pR / C

    // Get clock corrections
    var tCorr = satClockErrorCorrection(txRaw, satellite)

    tCorr -= getTgds(satellite)

    var txGPS = txRaw - tCorr

    // Compute again the clock bias
    tCorr = satClockErrorCorrection(txGPS, satellite)
    tCorr -= satellite.satelliteEphemeris.tgdS

    // Get the satellite coordinates (corrected) and velocity
    val satPos = satPos(txGPS, satellite)
    var x = satPos.x
    val vel = satPos.vel

    val xVec = DMatrixRMaj.wrap(3, 1, x)
    val velVec = DMatrixRMaj.wrap(3, 1, vel)

    // Get the satellite relativistic clock correction
    val tRel: Double = -2 * (CommonOps_DDRM.dot(xVec, velVec) / (C * C))

    // Account for the relativistic effect on the satellite clock bias and the time of transmission
    tCorr += tRel
    txGPS = txRaw - tCorr

    // Recompute the satellite coordinates with the corrected time and some additional
    // correction (i.e. Sagnac effect / rotation correction)
    val travelTime = tow - txGPS
    x = earthRotCorr(travelTime, x)

    return CtrlCorr(
        EcefLocation(x[0], x[1], x[2]),
        tCorr
    )
}

private fun getTgds(satellite: SatelliteMeasurements): Double {
    return if (satellite.constellation == PvtConstants.GALILEO
        && satellite.carrierFreq == PvtConstants.L5_FREQ
    ) satellite.satelliteEphemeris.tgdS * (PvtConstants.L1_FREQ / PvtConstants.L5_FREQ).pow(2)
    else satellite.satelliteEphemeris.tgdS
}

fun satClockErrorCorrection(time: Double, satellite: SatelliteMeasurements): Double {
    var corr: Double
    with(satellite.satelliteEphemeris) {
        var dt = 0.0
        this.keplerModel?.let {
            dt = checkTime(time - it.toeS)
        }
        corr = (af2 * dt + af1) * dt + af0
    }
    return corr
}

data class CtrlCorr(
    var ecefLocation: EcefLocation,
    var tCorr: Double = 0.0
)

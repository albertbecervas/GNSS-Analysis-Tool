package com.abecerra.pvt_acquisition.domain.acquisition

import android.location.GnssMeasurement
import android.os.Build
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.utils.PvtConstants


fun Epoch.mapGnssMeasurements(measurements: Collection<GnssMeasurement>) {
    with(measurements) {
        this.forEach {
            it.getSatellite(timeNanosGnss)?.let { sat ->
                satellitesMeasurements.add(sat)
            }
        }
    }
}


private fun GnssMeasurement.getSatellite(timeNanosGnss: Double): SatelliteMeasurements? {
    return if (!hasMultiPathOrUncertainty(this)) {
        buildSatellite(this, timeNanosGnss, state)
    } else null
}

private fun hasMultiPathOrUncertainty(gnssMeasurement: GnssMeasurement): Boolean {
    return with(gnssMeasurement) {
        multipathIndicator == GnssMeasurement.MULTIPATH_INDICATOR_DETECTED
                && receivedSvTimeUncertaintyNanos == PvtConstants.UNCERTAINTY_THR
    }
}

private fun buildSatellite(
    meas: GnssMeasurement,
    timeNanosGnss: Double,
    state: Int
): SatelliteMeasurements? {
    var satelliteMeasurements: SatelliteMeasurements? = null
    with(meas) {
        val tTx = getTxTime(timeOffsetNanos, receivedSvTimeNanos)
        val tRx = getRxTime(state, timeNanosGnss)
        tRx?.let {
            satelliteMeasurements = SatelliteMeasurements(
                svid = svid,
                state = state,
                multiPath = multipathIndicator,
                carrierFreq = if (meas.hasCarrierFrequencyHz()) carrierFrequencyHz.toDouble() else Constants.L1_FREQ,
                tTx = tTx,
                tRx = tRx,
                cn0 = cn0DbHz,
                pR = getPseudoRange(tTx, tRx)
            )
        }
    }
    return satelliteMeasurements
}

private fun getTxTime(timeOffsetNanos: Double, receivedSvTimeNanos: Long) =
    timeOffsetNanos + receivedSvTimeNanos

private fun getRxTime(state: Int, timeNanosGnss: Double): Double? {
    return when {
        checkTowDecode(state) || checkTowKnown(state) -> {
            timeNanosGnss.rem(PvtConstants.WEEK_NANOS)
        }
        checkGalState(state) -> {
            timeNanosGnss.rem(PvtConstants.GAL_E1C)
        }
        else -> null
    }
}


private fun checkTowDecode(state: Int): Boolean {
    // Check if binary state has 3rd bit 1
    return (state and GnssMeasurement.STATE_TOW_DECODED) == GnssMeasurement.STATE_TOW_DECODED
}

private fun checkTowKnown(state: Int): Boolean {
    // Check if binary state has 14th bit 1
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (state and GnssMeasurement.STATE_TOW_KNOWN) == GnssMeasurement.STATE_TOW_KNOWN
    } else {
        false
    }
}

private fun checkGalState(state: Int): Boolean {
    // Check if binary state has 14th bit 1
    return (state and GnssMeasurement.STATE_GAL_E1C_2ND_CODE_LOCK) == GnssMeasurement.STATE_GAL_E1C_2ND_CODE_LOCK
}

private fun getPseudoRange(tTx: Double, tRx: Double): Double = ((tRx - tTx) / 1e9) * Constants.C

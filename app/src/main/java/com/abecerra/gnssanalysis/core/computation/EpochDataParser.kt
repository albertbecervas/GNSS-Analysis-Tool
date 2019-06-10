package com.abecerra.gnssanalysis.core.computation

import android.annotation.SuppressLint
import android.location.GnssClock
import android.location.GnssMeasurement
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.os.Build
import com.abecerra.pvt.computation.data.*
import com.abecerra.pvt.computation.utils.Constants
import com.abecerra.pvt.computation.utils.Constants.L1_FREQ
import com.abecerra.pvt.suplclient.ephemeris.EphemerisResponse
import kotlin.math.floor
import kotlin.math.roundToInt

object EpochDataParser {

    @SuppressLint("WrongConstant")
    fun parseEpoch(
        gnssMeasurementEvent: GnssMeasurementsEvent,
        gnssStatus: GnssStatus,
        ephemerisResponse: EphemerisResponse
    ): Epoch {

        val measurement = Epoch()

        with(gnssMeasurementEvent) {

            with(clock) {
                measurement.timeNanosGnss = timeNanos - (getBiasNanosOrZero() + fullBiasNanos)
                measurement.tow = floor(measurement.timeNanosGnss.rem(WEEK_NANOS)) / 1e9
                measurement.now = floor(measurement.timeNanosGnss / WEEK_NANOS)
            }

            with(measurements) {
                this.forEach {
                    it.getSatellite(measurement.timeNanosGnss)?.let { sat ->
                        measurement.satellitesMeasurements.add(sat)
                    }
                }
            }
        }

        with(gnssStatus) {
            repeat(satelliteCount) { i ->
                measurement.satellitesMeasurements.find { it.svid == getSvid(i) }?.let {
                    it.constellation = getConstellationType(i)
                    it.azimuth = getAzimuthDegrees(i).roundToInt()
                    it.elevation = getElevationDegrees(i).roundToInt()
                }
            }
        }

        with(ephemerisResponse) {
            this.ephList.forEach { eph ->
                measurement.satellitesMeasurements.find { it.svid == eph.svid }?.satelliteEphemeris?.parseEphemeris(eph)
            }
        }

        return measurement

    }

    private fun GnssClock.getBiasNanosOrZero(): Double = if (hasBiasNanos()) biasNanos else 0.0

    private fun GnssMeasurement.getSatellite(timeNanosGnss: Double): SatelliteMeasurements? {
        return if (!hasMultiPathOrUncertainty(this)) {
            buildSatellite(this, timeNanosGnss, state)
        } else null

    }

    private fun hasMultiPathOrUncertainty(gnssMeasurement: GnssMeasurement): Boolean {
        return with(gnssMeasurement) {
            multipathIndicator == GnssMeasurement.MULTIPATH_INDICATOR_DETECTED
                    && receivedSvTimeUncertaintyNanos == UNCERTAINTY_THR
        }
    }

    private fun buildSatellite(meas: GnssMeasurement, timeNanosGnss: Double, state: Int): SatelliteMeasurements? {
        var satelliteMeasurements: SatelliteMeasurements? = null
        with(meas) {
            val tTx = getTxTime(timeOffsetNanos, receivedSvTimeNanos)
            val tRx = getRxTime(state, timeNanosGnss)
            tRx?.let {
                satelliteMeasurements = SatelliteMeasurements(
                    svid = svid,
                    state = state,
                    multiPath = multipathIndicator,
                    carrierFreq = if (meas.hasCarrierFrequencyHz()) carrierFrequencyHz.toDouble() else L1_FREQ,
                    tTx = tTx,
                    tRx = tRx,
                    cn0 = cn0DbHz,
                    pR = getPseudoRange(tTx, tRx)
                )
            }
        }
        return satelliteMeasurements
    }

    private fun getTxTime(timeOffsetNanos: Double, receivedSvTimeNanos: Long) = timeOffsetNanos + receivedSvTimeNanos
    private fun getRxTime(state: Int, timeNanosGnss: Double): Double? {
        return when {
            checkTowDecode(state) || checkTowKnown(state) -> {
                timeNanosGnss.rem(WEEK_NANOS)
            }
            checkGalState(state) -> {
                timeNanosGnss.rem(GAL_E1C)
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

}
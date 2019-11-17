package com.abecerra.pvt_acquisition.domain.acquisition

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.EphemerisResponse

object EpochAcquisitionDataBuilder {

    fun mapToEpoch(
        gnssMeasurementEvent: GnssMeasurementsEvent,
        gnssStatus: GnssStatus,
        ephemerisResponse: EphemerisResponse
    ): Epoch {

        val epoch = Epoch()

        with(epoch) {
            mapClockMeasurements(gnssMeasurementEvent.clock)

            mapGnssMeasurements(gnssMeasurementEvent.measurements)

            mapGnssStatus(gnssStatus)

            mapEphemerisResponse(ephemerisResponse)

            val filtered = satellitesMeasurements.filter { it.satelliteEphemeris.tow != -1.0 }
            satellitesMeasurements.clear()
            satellitesMeasurements.addAll(filtered)
        }

        return epoch
    }
}

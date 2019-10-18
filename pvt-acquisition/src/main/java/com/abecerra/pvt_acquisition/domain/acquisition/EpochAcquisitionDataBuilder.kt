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
        }

        return epoch
    }
}

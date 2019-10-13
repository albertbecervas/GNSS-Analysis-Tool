package com.abecerra.pvt_acquisition.acquisition

import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import com.abecerra.pvt_computation.data.output.PvtOutputData

interface GnssServiceOutput {

    interface GnssEventsListener {

        fun onGnssStarted()

        fun onGnssStopped()

        fun onSatelliteStatusChanged(status: GnssStatus)

        fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent)

        fun onSensorEvent(event: SensorEvent)

        fun onNmeaMessageReceived(message: String, timestamp: Long)

        fun onLocationReceived(location: Location)
    }

    interface PvtListener {

        fun onPvtResponse(pvtResponse: List<PvtOutputData>)

        fun onPvtError(error: String)

        fun onEphemerisObtained()

        fun onEphemerisError()
    }
}

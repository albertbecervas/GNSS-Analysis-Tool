package com.abecerra.gnssanalysis.core.computation

import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse

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

        fun onPvtResponse(pvtResponse: PvtResponse)

        fun onPvtError(error: String)

    }
}
package com.abecerra.gnssanalysis.core.computation

import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import com.abecerra.pvt.computation.data.ComputedPvtData

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

        fun onPvtResponse(pvtResponse: List<ComputedPvtData>)

        fun onPvtError(error: String)

        fun onEphemerisObtained()

        fun onEphemerisError()

    }
}
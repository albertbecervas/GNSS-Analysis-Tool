package com.abecerra.pvt_acquisition.data.inari

import android.location.GnssClock
import android.location.GnssMeasurement
import android.location.GnssStatus
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.EphemerisResponse
import java.util.*

data class GnssData(
    var modes: List<Mode> = arrayListOf(),
    var location: RefLocation? = null,
    var avgEnabled: Boolean = true,
    var avg: Int = 0,
    var elevationMask: Int = 15,
    var cnoMask: Int = 0,
    @Transient var ephemerisResponse: EphemerisResponse? = null,
    @Transient var measurements: ArrayList<MeasurementData> = arrayListOf(),
    @Transient var lastEphemerisDate: Date = Date(),
    @Transient var lastGnssStatus: GnssStatus? = null
)

data class MeasurementData(
    var gnssStatus: GnssStatus? = null,
    var gnssMeasurements: Collection<GnssMeasurement>? = null,
    var gnssClock: GnssClock? = null
)

data class RefLocation(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var altitude: Double? = null
)

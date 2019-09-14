package com.abecerra.gnssanalysis.core.computation.data

import android.location.GnssStatus
import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.Epoch
import com.abecerra.pvt.computation.data.GnssData
import com.abecerra.pvt.computation.data.Location
import com.abecerra.pvt.suplclient.ephemeris.EphemerisResponse
import java.util.*

data class GnssComputationData(
    var cn0mask: Int = 0,
    var elevationMask: Int = 0,
    var refLocation: Location = Location(),
    var startedComputingDate: Date = Date(),
    var computationSettings: List<ComputationSettings> = arrayListOf(),
    var gnssStatus: GnssStatus? = null,
    var ephemerisResponse: EphemerisResponse? = null,
    var epochs: ArrayList<Epoch> = arrayListOf()
) {

    fun parseToGnssData(): GnssData {
        return with(this) {
            GnssData(
                cn0mask = cn0mask,
                elevationMask = elevationMask,
                refLocation = refLocation,
                computationSettings = computationSettings,
                epochMeasurements = epochs
            )
        }
    }
}

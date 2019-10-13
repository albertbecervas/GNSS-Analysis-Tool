package com.abecerra.pvt_acquisition.data

import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.data.Location
import com.abecerra.pvt_computation.suplclient.ephemeris.EphemerisResponse
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

    fun parseToGnssData(): PvtInputData {
        return with(this) {
            PvtInputData(
                cn0mask = cn0mask,
                elevationMask = elevationMask,
                refLocation = refLocation,
                computationSettings = computationSettings,
                epochMeasurements = epochs
            )
        }
    }
}

package com.abecerra.pvt.computation

import com.abecerra.pvt.computation.data.ComputedPositionData
import com.abecerra.pvt.computation.data.GNSSData
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.ephemeris.EphemerisClient

class PVTEngine {

    private val gnssData: GNSSData = GNSSData()
    private val ephemerisClient: EphemerisClient = EphemerisClient()

    fun startComputing(refLocation: LlaLocation) {
        gnssData.refLocation = refLocation

        ephemerisClient.getEphemerisData(refLocation, {
            obtainPosition()
        }, {
            print(it)
        })

    }

    fun obtainPosition(): ComputedPositionData {
        return ComputedPositionData(
            LlaLocation()
        )
    }

}
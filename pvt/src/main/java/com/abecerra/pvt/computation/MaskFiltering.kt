package com.abecerra.pvt.computation

import com.abecerra.pvt.computation.data.GnssData

object MaskFiltering {

    fun filter(gnssData: GnssData): GnssData {

        gnssData.epochMeasurements.forEach {

            val filteredSatellites = it.satellitesMeasurements.filter { sat ->
                sat.cn0 >= gnssData.cn0mask && sat.elevation >= gnssData.elevationMask
            }
            it.satellitesMeasurements.clear()
            it.satellitesMeasurements.addAll(filteredSatellites)
        }

        return gnssData
    }


}


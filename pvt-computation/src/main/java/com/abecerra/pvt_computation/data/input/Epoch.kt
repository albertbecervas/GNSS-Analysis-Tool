package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.data.Constants

data class Epoch(
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0,
    var now: Double = 0.0,
    var ionoProto: ArrayList<Double> = arrayListOf(),
    var satellitesMeasurements: ArrayList<SatelliteMeasurements> = arrayListOf()
) {
    fun getGpsSatelliteMeasurements(): List<SatelliteMeasurements> {
        return satellitesMeasurements.filter { it.constellation == Constants.GPS }
    }
}

package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.data.PvtConstants

data class Epoch(
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0,
    var now: Double = 0.0,
    var ionoProto: ArrayList<Double> = arrayListOf(),
    var satellitesMeasurements: ArrayList<SatelliteMeasurements> = arrayListOf()
) {
    fun getConstellationSatellites(constellation: Int): List<SatelliteMeasurements> {
        return when (constellation) {
            PvtConstants.GPS -> getGpsSatelliteMeasurements()
            PvtConstants.GALILEO -> getGalSatelliteMeasurements()
            else -> arrayListOf()
        }
    }

    private fun getGpsSatelliteMeasurements(): List<SatelliteMeasurements> {
        return satellitesMeasurements.filter { it.constellation == PvtConstants.GPS }
    }

    private fun getGalSatelliteMeasurements(): List<SatelliteMeasurements> {
        return satellitesMeasurements.filter { it.constellation == PvtConstants.GALILEO }
    }
}

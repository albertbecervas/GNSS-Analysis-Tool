package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.data.PvtConstants

data class Epoch(
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0,
    var now: Double = 0.0,
    var ionoProto: ArrayList<Double> = arrayListOf(),
    var satellitesMeasurements: ArrayList<SatelliteMeasurements> = arrayListOf()
) {
    fun getConstellationSatellitesForBand(constellation: Int, band: List<Int>)
            : List<SatelliteMeasurements> {
        return when (constellation) {
            PvtConstants.GPS -> getGpsSatelliteMeasurements(band)
            PvtConstants.GALILEO -> getGalSatelliteMeasurements(band)
            else -> arrayListOf()
        }
    }

    private fun getGpsSatelliteMeasurements(bands: List<Int>): List<SatelliteMeasurements> {
        return satellitesMeasurements.filter {
            if (bands.contains(PvtConstants.BAND_L1)) {
                it.constellation == PvtConstants.CONST_GPS && it.carrierFreq == PvtConstants.L1_FREQ
            } else {
                it.constellation == PvtConstants.CONST_GPS && it.carrierFreq == PvtConstants.L5_FREQ
            }
        }
    }

    private fun getGalSatelliteMeasurements(bands: List<Int>): List<SatelliteMeasurements> {
        return satellitesMeasurements.filter {
            if (bands.contains(PvtConstants.BAND_E1)) {
                it.constellation == PvtConstants.CONST_GAL && it.carrierFreq == PvtConstants.L1_FREQ
            } else {
                it.constellation == PvtConstants.CONST_GAL && it.carrierFreq == PvtConstants.L5_FREQ
            }
        }
    }

}

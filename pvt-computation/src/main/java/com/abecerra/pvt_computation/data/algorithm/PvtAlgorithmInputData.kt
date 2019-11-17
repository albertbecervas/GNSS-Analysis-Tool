package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.Location
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch

class PvtAlgorithmInputData(
    val epochMeasurements: ArrayList<Epoch>,
    val referenceLocation: Location,
    val computationSettings: ComputationSettings
) {

    fun isGpsSelected(): Boolean {
        return computationSettings.constellations.size == 1
                && computationSettings.constellations.contains(PvtConstants.GPS)
    }

    fun isGalileoOnlySelected(): Boolean {
        return computationSettings.constellations.size == 1
                && computationSettings.constellations.contains(PvtConstants.GALILEO)
    }

    fun isMultiConstellationSelected(): Boolean {
        return computationSettings.constellations.containsAll(
            listOf(PvtConstants.GPS, PvtConstants.GALILEO)
        )
    }

    fun isWeightedLeastSquaresSelected(): Boolean {
        return computationSettings.algorithm == PvtConstants.ALG_WLS
    }

    fun getSelectedBandForGps(): Int {
        return if (computationSettings.bands.contains(PvtConstants.BAND_L1)){
            PvtConstants.BAND_L1
        } else PvtConstants.BAND_L5
    }

    fun getSelectedBandForGal(): Int {
        return if (computationSettings.bands.contains(PvtConstants.BAND_E1)){
            PvtConstants.BAND_E1
        } else PvtConstants.BAND_E5A
    }

    fun isL1BandSelected(): Boolean{
        return computationSettings.bands.contains(PvtConstants.BAND_L1)
    }

    fun isL5BandSelected(): Boolean{
        return computationSettings.bands.contains(PvtConstants.BAND_E5A)
    }

    fun isE1BandSelected(): Boolean{
        return computationSettings.bands.contains(PvtConstants.BAND_E1)
    }

    fun isE5BandSelected(): Boolean{
        return computationSettings.bands.contains(PvtConstants.BAND_E5A)
    }

}

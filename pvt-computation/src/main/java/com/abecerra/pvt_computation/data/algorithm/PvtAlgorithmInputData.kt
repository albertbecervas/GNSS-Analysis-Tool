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
}

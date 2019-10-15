package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.data.Location
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch

class PvtAlgorithmInputData(
    val epochMeasurements: ArrayList<Epoch>,
    val referenceLocation: Location,
    val computationSettings: ComputationSettings
) {

    fun isGpsSelected(): Boolean {
        return computationSettings.constellations.contains(Constants.GPS)
    }

    fun isGalileoSelected(): Boolean {
        return computationSettings.constellations.contains(Constants.GALILEO)
    }

    fun isMultiConstellationSelected(): Boolean {
        return computationSettings.constellations.containsAll(
            listOf(Constants.GPS, Constants.GALILEO)
        )
    }

    fun isWeightedLeastSquaresSelected(): Boolean {
        return computationSettings.algorithm == Constants.ALG_LS
    }
}

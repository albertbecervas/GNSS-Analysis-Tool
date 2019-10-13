package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.domain.computation.data.Location

data class PvtInputData(
    var cn0mask: Int = 0,
    var elevationMask: Int = 0,
    var refLocation: Location = Location(),
    var computationSettings: List<ComputationSettings> = arrayListOf(),
    var epochMeasurements: ArrayList<Epoch> = arrayListOf()
)

package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch

data class PvtAlgorithmInputData(
    val epochMeasurements: ArrayList<Epoch>,
    val computationSettings: ComputationSettings
)

package com.abecerra.pvt.computation.data

import java.io.Serializable

data class ComputedPvtData(
    var pvtFix: PvtFix,
    var refPosition: LlaLocation,
    var computationSettings: ComputationSettings
) : Serializable
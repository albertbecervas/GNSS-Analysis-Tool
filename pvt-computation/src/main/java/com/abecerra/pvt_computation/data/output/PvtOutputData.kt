package com.abecerra.pvt_computation.data.output

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.domain.computation.data.LlaLocation
import com.abecerra.pvt_computation.domain.computation.data.PvtFix
import java.io.Serializable

data class PvtOutputData(
    var pvtFix: PvtFix,
    var refPosition: LlaLocation,
    var computationSettings: ComputationSettings
) : Serializable

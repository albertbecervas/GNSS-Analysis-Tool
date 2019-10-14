package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.Location
import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData

class PvtComputationAlgorithmImpl : PvtComputationAlgorithm {
    override fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtFix {
        return PvtFix(Location(LlaLocation(47.2), EcefLocation(2.2)), 0.0)
    }
}

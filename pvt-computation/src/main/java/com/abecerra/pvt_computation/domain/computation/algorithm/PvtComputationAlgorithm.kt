package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData

interface PvtComputationAlgorithm {

    fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtFix

}

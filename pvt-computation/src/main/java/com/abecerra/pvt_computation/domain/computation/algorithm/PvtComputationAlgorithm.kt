package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData

interface PvtComputationAlgorithm {

    fun executePvtAlgorithm(algorithmInputData: PvtAlgorithmInputData): PvtAlgorithmOutputData

}

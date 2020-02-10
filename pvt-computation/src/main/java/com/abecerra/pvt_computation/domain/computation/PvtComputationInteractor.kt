package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData

interface PvtComputationInteractor {

    fun computePosition(pvtInputData: PvtInputData): List<PvtOutputData>

}

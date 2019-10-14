package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import io.reactivex.Single

interface PvtComputationInteractor {

    fun computePosition(pvtInputData: PvtInputData): Single<List<PvtOutputData>>

}

package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.data.*
import io.reactivex.Single

object PvtEngine {

    fun computePosition(pvtInputData: PvtInputData): Single<List<PvtOutputData>> {
        return Single.create {

            val computedPvtDataList = arrayListOf<PvtOutputData>()

//            val filteredGnssData = MaskFiltering.filter(pvtInputData)

            pvtInputData.computationSettings.forEach { computationSettings ->
                val computedPvtData = PvtOutputData(
                    pvtFix = PvtFix(Location(LlaLocation(47.2), EcefLocation(2.2)), 0.0),
                    refPosition = LlaLocation(47.0, 2.0),
                    computationSettings = computationSettings
                )
                computedPvtDataList.add(computedPvtData)
            }

            it.onSuccess(computedPvtDataList)

        }
    }

}

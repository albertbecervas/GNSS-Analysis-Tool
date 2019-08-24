package com.abecerra.pvt.computation

import com.abecerra.pvt.computation.data.*
import io.reactivex.Single

object PvtEngine {

    fun computePosition(gnssData: GnssData): Single<List<ComputedPvtData>> {
        return Single.create {

            val computedPvtDataList = arrayListOf<ComputedPvtData>()

//            val filteredGnssData = MaskFiltering.filter(gnssData)

            gnssData.computationSettings.forEach { computationSettings ->
                val computedPvtData = ComputedPvtData(
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
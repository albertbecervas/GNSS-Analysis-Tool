package com.abecerra.pvt.computation

import com.abecerra.pvt.computation.data.ComputedPvtData
import com.abecerra.pvt.computation.data.GnssData
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.data.PvtFix
import io.reactivex.Single

object PvtEngine {

    fun computePosition(gnssData: GnssData): Single<ComputedPvtData> {
        return Single.create {

            val filteredGnssData = MaskFiltering.filter(gnssData)

            gnssData.computationSettings.forEach {

            }

        }
    }

}
package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.Location
import com.abecerra.pvt_computation.data.PvtFix
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputationAlgorithm
import io.reactivex.Single

class PvtComputationInteractorImpl(private val pvtComputationAlgorithm: PvtComputationAlgorithm) :
    PvtComputationInteractor {

    override fun computePosition(pvtInputData: PvtInputData): Single<List<PvtOutputData>> {
        return Single.create {

            val computedPvtDataList = arrayListOf<PvtOutputData>()

//            val filteredGnssData = MaskFiltering.filter(pvtInputData)

            pvtInputData.computationSettings.forEach { computationSettings ->

                val pvtAlgorithmInputData = PvtAlgorithmInputData(
                    epochMeasurements = pvtInputData.epochMeasurements,
                    computationSettings = computationSettings
                )

                val pvtFix = pvtComputationAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)

//                val computedPvtData = PvtOutputData(
//                    pvtFix = pvtFix,
//                    refPosition = pvtInputData.refLocation.llaLocation,
//                    computationSettings = computationSettings
//                )
//                computedPvtDataList.add(computedPvtData)
            }

            it.onSuccess(computedPvtDataList)
        }
    }

}

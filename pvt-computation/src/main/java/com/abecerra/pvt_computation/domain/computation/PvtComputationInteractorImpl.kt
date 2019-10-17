package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputationAlgorithm
import com.abecerra.pvt_computation.domain.computation.filter.MaskFiltering

class PvtComputationInteractorImpl(private val pvtComputationAlgorithm: PvtComputationAlgorithm) :
    PvtComputationInteractor {

    override fun computePosition(pvtInputData: PvtInputData): List<PvtOutputData> {

        val computedPvtDataList = arrayListOf<PvtOutputData>()

        val filteredPvtInputData = MaskFiltering.filter(pvtInputData)

        pvtInputData.computationSettings.forEach { computationSettings ->

            val pvtAlgorithmInputData = PvtAlgorithmInputData(
                epochMeasurements = filteredPvtInputData.epochMeasurements,
                referenceLocation = filteredPvtInputData.refLocation,
                computationSettings = computationSettings
            )

            val pvtOutput = pvtComputationAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)

            with(pvtOutput) {
                val pvtOutputData = PvtOutputData(
                    pvtFix = pvtFix,
                    refPosition = pvtInputData.refLocation.llaLocation,
                    computationSettings = computationSettings
                )
                computedPvtDataList.add(pvtOutputData)
            }
        }

        return computedPvtDataList
    }
}

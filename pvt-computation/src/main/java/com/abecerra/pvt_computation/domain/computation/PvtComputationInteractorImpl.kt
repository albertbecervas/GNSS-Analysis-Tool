package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper.mapFromPvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtComputationAlgorithm
import com.abecerra.pvt_computation.domain.computation.filter.MaskFiltering

class PvtComputationInteractorImpl(private val pvtComputationAlgorithm: PvtComputationAlgorithm) :
    PvtComputationInteractor {

    override fun computePosition(pvtInputData: PvtInputData): List<PvtOutputData> {

        val computedPvtDataList = arrayListOf<PvtOutputData>()

        val filteredPvtInputData = MaskFiltering.filter(pvtInputData)

        pvtInputData.computationSettings.forEachIndexed { index, settings ->

            getPvtOutputDataFromPvtAlgorithm(filteredPvtInputData, index, settings)?.let {
                computedPvtDataList.add(it)
            }
        }

        return computedPvtDataList
    }

    private fun getPvtOutputDataFromPvtAlgorithm(
        filteredPvtInputData: PvtInputData, index: Int, computationSettings: ComputationSettings
    ): PvtOutputData? {
        val pvtAlgorithmInputData = mapFromPvtInputData(filteredPvtInputData, index)

        val pvtOutput = pvtComputationAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)

        return pvtOutput?.pvtFix?.let { pvtFix ->
            PvtOutputData(pvtFix, filteredPvtInputData.refLocation.llaLocation, computationSettings)
        }
    }
}

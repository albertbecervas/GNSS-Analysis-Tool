package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper.mapFromPvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.data.output.mapper.PvtAlgorithmOutputDataMapper.mapToPvtOutputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtAlgorithm
import com.abecerra.pvt_computation.domain.computation.filter.MaskFiltering

class PvtComputationInteractorImpl(private val pvtAlgorithm: PvtAlgorithm) :
    PvtComputationInteractor {

    override fun computePosition(pvtInputData: PvtInputData): List<PvtOutputData> {

        // Init an results list.
        val computedPvtDataList = arrayListOf<PvtOutputData>()

        // Apply masks before computing.
        val filteredPvtInputData = MaskFiltering.filter(pvtInputData)

        // Execute PVT algorithm for each setting.
        pvtInputData.computationSettings.forEach { settings ->
            executePvtAlgorithm(filteredPvtInputData, settings)?.let {
                computedPvtDataList.add(it)
            }
        }

        return computedPvtDataList
    }

    private fun executePvtAlgorithm(
        pvtInputData: PvtInputData, computationSettings: ComputationSettings
    ): PvtOutputData? {
        val pvtAlgorithmInputData = mapFromPvtInputData(pvtInputData, computationSettings)

        return pvtAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)?.let {
            mapToPvtOutputData(pvtInputData, it, computationSettings)
        }
    }
}

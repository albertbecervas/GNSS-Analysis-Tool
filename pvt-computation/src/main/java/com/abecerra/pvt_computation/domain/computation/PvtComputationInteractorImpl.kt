package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper.mapFromPvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtAlgorithm
import com.abecerra.pvt_computation.domain.computation.filter.MaskFiltering

class PvtComputationInteractorImpl(private val pvtAlgorithm: PvtAlgorithm) :
    PvtComputationInteractor {

    override fun computePosition(pvtInputData: PvtInputData): List<PvtOutputData> {

        val computedPvtDataList = arrayListOf<PvtOutputData>()

        val filteredPvtInputData = MaskFiltering.filter(pvtInputData)

        pvtInputData.computationSettings.forEach { settings ->
            executePvtAlgorithm(filteredPvtInputData, settings)?.let {
                computedPvtDataList.add(it)
            }
        }

        return computedPvtDataList
    }

    private fun executePvtAlgorithm(
        filteredPvtInputData: PvtInputData, computationSettings: ComputationSettings
    ): PvtOutputData? {
        val pvtAlgorithmInputData = mapFromPvtInputData(filteredPvtInputData, computationSettings)

        return pvtAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)?.let {
            with(it) {
                PvtOutputData(
                    pvtFix, filteredPvtInputData.refLocation.llaLocation, computationSettings,
                    corrections, dop, residue, nSatellites, gpsTime
                )
            }
        }
    }
}

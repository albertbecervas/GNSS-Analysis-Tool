package com.abecerra.pvt_computation.data.input.mapper

import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.PvtInputData

object PvtAlgorithmInputDataMapper {

    fun mapFromPvtInputData(
        pvtInputData: PvtInputData, computationSettings: ComputationSettings
    ): PvtAlgorithmInputData {
        return with(pvtInputData) {
            PvtAlgorithmInputData(
                epochMeasurements = epochMeasurements,
                referenceLocation = refLocation,
                computationSettings = computationSettings
            )
        }
    }
}

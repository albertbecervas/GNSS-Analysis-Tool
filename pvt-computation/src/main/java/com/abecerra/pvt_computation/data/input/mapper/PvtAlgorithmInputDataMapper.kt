package com.abecerra.pvt_computation.data.input.mapper

import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmInputData
import com.abecerra.pvt_computation.data.input.PvtInputData

object PvtAlgorithmInputDataMapper {

    fun mapFromPvtInputData(pvtInputData: PvtInputData, index: Int): PvtAlgorithmInputData{
        return with(pvtInputData){
            PvtAlgorithmInputData(
                epochMeasurements = pvtInputData.epochMeasurements,
                referenceLocation = pvtInputData.refLocation,
                computationSettings = computationSettings[index]
            )
        }
    }

}

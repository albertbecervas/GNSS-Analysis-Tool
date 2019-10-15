package com.abecerra.pvt_acquisition.data.mapper

import com.abecerra.pvt_acquisition.data.GnssComputationData
import com.abecerra.pvt_computation.data.input.PvtInputData

object PvtInputDataMapper {

    fun mapToPvtInputData(gnssComputationData: GnssComputationData): PvtInputData {
        return with(gnssComputationData) {
            PvtInputData(
                cn0mask = cn0mask,
                elevationMask = elevationMask,
                refLocation = refLocation,
                computationSettings = computationSettings,
                epochMeasurements = epochs
            )
        }
    }
}

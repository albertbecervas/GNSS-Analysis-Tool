package com.abecerra.pvt_computation.domain.computation

import com.abecerra.pvt_computation.data.input.PvtInputData

object MaskFiltering {

    fun filter(pvtInputData: PvtInputData): PvtInputData {

        pvtInputData.epochMeasurements.forEach {

            val filteredSatellites = it.satellitesMeasurements.filter { sat ->
                sat.cn0 >= pvtInputData.cn0mask && sat.elevation >= pvtInputData.elevationMask
            }
            it.satellitesMeasurements.clear()
            it.satellitesMeasurements.addAll(filteredSatellites)
        }

        return pvtInputData
    }


}


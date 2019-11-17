package com.abecerra.pvt_computation.data.output.mapper

import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.output.PvtOutputData

object PvtAlgorithmOutputDataMapper {

    fun mapToPvtOutputData(
        pvtInputData: PvtInputData, pvtAlgorithmOutputData: PvtAlgorithmOutputData,
        computationSettings: ComputationSettings
    ): PvtOutputData {
        return with(pvtAlgorithmOutputData) {
            PvtOutputData(
                pvtFix = pvtFix,
                refPosition = pvtInputData.refLocation.llaLocation,
                computationSettings = computationSettings,
                corrections = corrections,
                dop = dop,
                residue = residue,
                nSats = nSatellites,
                gpsTime = gpsTime
            )
        }
    }
}

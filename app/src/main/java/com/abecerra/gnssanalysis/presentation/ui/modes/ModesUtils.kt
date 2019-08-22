package com.abecerra.gnssanalysis.presentation.ui.modes

import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.PositionParameters

fun addDefaultModes(): List<ComputationSettings> {

    val mode = ComputationSettings(
        0,
        "GPS LS",
        arrayListOf(PositionParameters.CONST_GPS),
        arrayListOf(PositionParameters.BAND_L1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_LS,
        isSelected = false
    )
    val mode2 = ComputationSettings(
        1,
        "Galileo E1",
        arrayListOf(PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_LS,
        isSelected = false
    )
    val mode3 = ComputationSettings(
        2,
        "GPS WLS",
        arrayListOf(PositionParameters.CONST_GPS),
        arrayListOf(PositionParameters.BAND_L1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_WLS,
        isSelected = false
    )

    val mode4 = ComputationSettings(
        3,
        "Galileo WLS",
        arrayListOf(PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_WLS,
        isSelected = false
    )

    val mode5 = ComputationSettings(
        4,
        "Multiconst Iono-Free",
        arrayListOf(PositionParameters.CONST_GPS, PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_L1, PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_TROPOSPHERE, PositionParameters.CORR_IONOFREE),
        PositionParameters.ALG_LS,
        isSelected = false
    )

    return arrayListOf(mode, mode2, mode3, mode4, mode5)

}
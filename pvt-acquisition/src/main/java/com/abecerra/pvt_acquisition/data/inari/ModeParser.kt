package com.abecerra.pvt_acquisition.data.inari

import com.abecerra.pvt_computation.data.input.ComputationSettings

object ModeParser {

    fun parseSettingsListToModeList(computationSettings: List<ComputationSettings>): List<Mode>{
        return computationSettings.map { parseSettingsToMode(it) }
    }

    private fun parseSettingsToMode(computationSettings: ComputationSettings): Mode{
        return with(computationSettings){
            Mode(
                id = 0,
                name = name,
                constellations = constellations,
                bands = bands,
                corrections = corrections,
                algorithm = algorithm,
                isSelected = isSelected,
                color = color
            )
        }
    }

}

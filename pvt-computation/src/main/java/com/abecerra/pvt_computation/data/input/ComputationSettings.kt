package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.data.Constants
import com.abecerra.pvt_computation.utils.PvtConstants

data class ComputationSettings(
    val name: String = "",
    val constellations: ArrayList<Int> = arrayListOf(),
    val bands: ArrayList<Int> = arrayListOf(),
    val corrections: ArrayList<Int> = arrayListOf(),
    val algorithm: Int = -1,
    var isSelected: Boolean = false,
    var color: Int = -1
) {
    /**
     * STRING GETTERS
     */

    fun constellationsAsString(): CharSequence? {
        var constellationsString = ""

        if (constellations.contains(PvtConstants.CONST_GPS)) {
            constellationsString = "GPS"
        }
        if (constellations.contains(PvtConstants.CONST_GAL)) {
            if (constellationsString.isNotBlank()) constellationsString += ", "
            constellationsString += "Galileo"
        }
        return constellationsString
    }

    fun bandsAsString(): CharSequence? {
        var bandsString = ""

        if (bands.contains(PvtConstants.BAND_L1)) {
            bandsString = "L1"
        }
        if (bands.contains(PvtConstants.BAND_L5)) {
            if (bandsString.isNotBlank()) bandsString += ", "
            bandsString += "L5"
        }
        return bandsString
    }

    fun correctionsAsString(): CharSequence? {
        var correctionsString = ""

        if (corrections.contains(Constants.CORR_IONOSPHERE)) {
            correctionsString = "Ionosphere"
        }
        if (corrections.contains(Constants.CORR_TROPOSPHERE)) {
            if (correctionsString.isNotBlank()) correctionsString += ", "
            correctionsString += "Troposphere"
        }
        if (corrections.contains(Constants.CORR_IONOFREE)) {
            if (correctionsString.isNotBlank()) correctionsString += ", "
            correctionsString += "Iono-free"
        }
        if (correctionsString.isBlank()) correctionsString = "None"
        return correctionsString
    }

    fun algorithmAsString(): CharSequence? {
        var algorithmString = ""

        when (algorithm) {
            Constants.ALG_LS -> algorithmString = "Least Squares"
            Constants.ALG_WLS -> algorithmString = "Weighted Least Squares"
        }
        return algorithmString
    }
}

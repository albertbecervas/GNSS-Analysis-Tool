package com.abecerra.pvt_acquisition.data.inari

import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.PvtConstants.ALG_LS
import com.abecerra.pvt_computation.data.PvtConstants.ALG_WLS
import com.abecerra.pvt_computation.data.PvtConstants.BAND_E1
import com.abecerra.pvt_computation.data.PvtConstants.BAND_E5A
import com.abecerra.pvt_computation.data.PvtConstants.BAND_L1
import com.abecerra.pvt_computation.data.PvtConstants.BAND_L5
import com.abecerra.pvt_computation.data.PvtConstants.CORR_IONOFREE
import com.abecerra.pvt_computation.data.PvtConstants.CORR_IONOSPHERE
import com.abecerra.pvt_computation.data.PvtConstants.CORR_TROPOSPHERE

data class Mode(
    var id: Int,
    val name: String = "",
    val constellations: ArrayList<Int>,
    val bands: ArrayList<Int>,
    val corrections: ArrayList<Int>,
    val algorithm: Int,
    var isSelected: Boolean,
    var color: Int = -1
) {
    /**
     * STRING GETTERS
     */

    fun constellationsAsString(): CharSequence? {
        var constellationsString = ""

        if (constellations.contains(PvtConstants.GPS)) {
            constellationsString = "GPS"
        }
        if (constellations.contains(PvtConstants.GALILEO)) {
            if (constellationsString.isNotBlank()) constellationsString += ", "
            constellationsString += "Galileo"
        }
        return constellationsString
    }

    fun bandsAsString(): CharSequence? {
        var bandsString = ""

        if (bands.contains(BAND_L1)) {
            if (bandsString.isNotBlank()) bandsString += ", "
            bandsString = "L1"
        }
        if (bands.contains(BAND_L5)) {
            if (bandsString.isNotBlank()) bandsString += ", "
            bandsString += "L5"
        }
        if (bands.contains(BAND_E1)) {
            if (bandsString.isNotBlank()) bandsString += ", "
            bandsString += "E1"
        }
        if (bands.contains(BAND_E5A)) {
            if (bandsString.isNotBlank()) bandsString += ", "
            bandsString += "E5a"
        }
        return bandsString
    }

    fun correctionsAsString(): CharSequence? {
        var correctionsString = ""

        if (corrections.contains(CORR_IONOSPHERE)) {
            correctionsString = "Ionosphere"
        }
        if (corrections.contains(CORR_TROPOSPHERE)) {
            if (correctionsString.isNotBlank()) correctionsString += ", "
            correctionsString += "Troposphere"
        }
        if (corrections.contains(CORR_IONOFREE)) {
            if (correctionsString.isNotBlank()) correctionsString += ", "
            correctionsString += "Iono-free"
        }
        if (correctionsString.isBlank()) correctionsString = "None"
        return correctionsString
    }

    fun algorithmAsString(): CharSequence? {
        var algorithmString = ""

        when (algorithm) {
            ALG_LS -> algorithmString = "Least Squares"
            ALG_WLS -> algorithmString = "Weighted Least Squares"
        }
        return algorithmString
    }

}

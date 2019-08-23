package com.abecerra.gnssanalysis.presentation.ui.modes

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.PositionParameters
import kotlinx.android.synthetic.main.dialog_new_mode.view.*
import org.jetbrains.anko.toast

fun addDefaultComputationSettings(): List<ComputationSettings> {

    val mode = ComputationSettings(
        "GPS LS",
        arrayListOf(PositionParameters.CONST_GPS),
        arrayListOf(PositionParameters.BAND_L1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_LS,
        isSelected = false
    )
    val mode2 = ComputationSettings(
        "Galileo E1",
        arrayListOf(PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_LS,
        isSelected = false
    )
    val mode3 = ComputationSettings(
        "GPS WLS",
        arrayListOf(PositionParameters.CONST_GPS),
        arrayListOf(PositionParameters.BAND_L1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_WLS,
        isSelected = false
    )

    val mode4 = ComputationSettings(
        "Galileo WLS",
        arrayListOf(PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_IONOSPHERE, PositionParameters.CORR_TROPOSPHERE),
        PositionParameters.ALG_WLS,
        isSelected = false
    )

    val mode5 = ComputationSettings(
        "Multiconst Iono-Free",
        arrayListOf(PositionParameters.CONST_GPS, PositionParameters.CONST_GAL),
        arrayListOf(PositionParameters.BAND_L1, PositionParameters.BAND_E1),
        arrayListOf(PositionParameters.CORR_TROPOSPHERE, PositionParameters.CORR_IONOFREE),
        PositionParameters.ALG_LS,
        isSelected = false
    )

    return arrayListOf(mode, mode2, mode3, mode4, mode5)

}

fun setComputationSettingsSelectedParams(layout: View?, dialog: AlertDialog, context: Context): ComputationSettings? {

    var name = ""
    val constellations = arrayListOf<Int>()
    val bands = arrayListOf<Int>()
    val corrections = arrayListOf<Int>()
    var algorithm = 0

    layout?.let {
        name = it.modeNameTextEdit.text.toString() // set the name
        if (it.constOption1.isChecked) constellations.add(PositionParameters.CONST_GPS) // set selected constellations
        if (it.constOption2.isChecked) constellations.add(PositionParameters.CONST_GAL)
        if (it.isEnabled && it.rbL1.isChecked) bands.add(PositionParameters.BAND_L1) // set selected bands
        if (it.isEnabled && it.rbL5.isChecked) bands.add(PositionParameters.BAND_L5)
        if (it.isEnabled && it.rbE1.isChecked) bands.add(PositionParameters.BAND_E1)
        if (it.isEnabled && it.rbE5a.isChecked) bands.add(PositionParameters.BAND_E5A)
        if (it.correctionsOption1.isChecked) corrections.add(PositionParameters.CORR_IONOSPHERE)  // set selected corrections
        if (it.correctionsOption2.isChecked) corrections.add(PositionParameters.CORR_TROPOSPHERE)
        if (it.correctionsOption3.isChecked) corrections.add(PositionParameters.CORR_IONOFREE)
        if (it.algorithm1.isChecked) algorithm = PositionParameters.ALG_LS  // set selected algorithm
        if (it.algorithm2.isChecked) algorithm = PositionParameters.ALG_WLS
    }
    val modesList = AppSharedPreferences.getInstance().getComputationSettingsList()
    return if (modeCanBeAdded(name, constellations, bands, modesList, context)) {
        ComputationSettings(name, constellations, bands, corrections, algorithm, false)
    } else {
        null
    }

}

fun modeCanBeAdded(
    name: String,
    constellations: ArrayList<Int>,
    bands: ArrayList<Int>,
    modesList: ArrayList<ComputationSettings>,
    context: Context
): Boolean {

    var canBeAdded = false
    with(context) {
        if (name.isNotBlank()) {
            if (!modesList.any { mode -> mode.name == name }) { //if name is not repeated
                if (constellations.isNotEmpty()) { // if one constellation is selected
                    if (bands.isNotEmpty()) { //if one band is selected
                        canBeAdded = true
                    } else { //if no band is selected
                        toast("At least one band must be selected")
                    }
                } else { //if no constellation is selected
                    toast("At least one constellattion must be selected")
                }
            } else { //if name already exists
                toast("This name already exists")
            }
        } else {//if name is blank
            toast("Name can not be blank")
        }
    }
    return canBeAdded
}

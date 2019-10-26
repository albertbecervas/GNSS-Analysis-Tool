package com.abecerra.gnssanalysis.presentation.ui.modes

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.abecerra.gnssanalysis.app.utils.AppSharedPreferences
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.PvtConstants
import kotlinx.android.synthetic.main.dialog_new_mode.view.*
import org.jetbrains.anko.toast

fun addDefaultComputationSettings(): List<ComputationSettings> {

    val mode = ComputationSettings(
        "GPS LS",
        arrayListOf(PvtConstants.GPS),
        arrayListOf(PvtConstants.BAND_L1),
        arrayListOf(PvtConstants.CORR_IONOSPHERE, PvtConstants.CORR_TROPOSPHERE),
        PvtConstants.ALG_LS,
        isSelected = false
    )
    val mode2 = ComputationSettings(
        "Galileo E1",
        arrayListOf(PvtConstants.GALILEO),
        arrayListOf(PvtConstants.BAND_E1),
        arrayListOf(PvtConstants.CORR_IONOSPHERE, PvtConstants.CORR_TROPOSPHERE),
        PvtConstants.ALG_LS,
        isSelected = false
    )
    val mode3 = ComputationSettings(
        "GPS WLS",
        arrayListOf(PvtConstants.GPS),
        arrayListOf(PvtConstants.BAND_L1),
        arrayListOf(PvtConstants.CORR_IONOSPHERE, PvtConstants.CORR_TROPOSPHERE),
        PvtConstants.ALG_WLS,
        isSelected = false
    )

    val mode4 = ComputationSettings(
        "Galileo WLS",
        arrayListOf(PvtConstants.GALILEO),
        arrayListOf(PvtConstants.BAND_E1),
        arrayListOf(PvtConstants.CORR_IONOSPHERE, PvtConstants.CORR_TROPOSPHERE),
        PvtConstants.ALG_WLS,
        isSelected = false
    )

    val mode5 = ComputationSettings(
        "Multiconst Iono-Free",
        arrayListOf(PvtConstants.GPS, PvtConstants.GALILEO),
        arrayListOf(PvtConstants.BAND_L1, PvtConstants.BAND_E1),
        arrayListOf(PvtConstants.CORR_TROPOSPHERE, PvtConstants.CORR_IONOFREE),
        PvtConstants.ALG_LS,
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
        if (it.constOption1.isChecked) constellations.add(PvtConstants.GPS) // set selected constellations
        if (it.constOption2.isChecked) constellations.add(PvtConstants.GALILEO)
        if (it.isEnabled && it.rbL1.isChecked) bands.add(PvtConstants.BAND_L1) // set selected bands
        if (it.isEnabled && it.rbL5.isChecked) bands.add(PvtConstants.BAND_L5)
        if (it.isEnabled && it.rbE1.isChecked) bands.add(PvtConstants.BAND_E1)
        if (it.isEnabled && it.rbE5a.isChecked) bands.add(PvtConstants.BAND_E5A)
        if (it.correctionsOption1.isChecked) corrections.add(PvtConstants.CORR_IONOSPHERE) // set selected corrections
        if (it.correctionsOption2.isChecked) corrections.add(PvtConstants.CORR_TROPOSPHERE)
        if (it.correctionsOption3.isChecked) corrections.add(PvtConstants.CORR_IONOFREE)
        if (it.algorithm1.isChecked) algorithm = PvtConstants.ALG_LS // set selected algorithm
        if (it.algorithm2.isChecked) algorithm = PvtConstants.ALG_WLS
    }
    val modesList = AppSharedPreferences.getInstance().getComputationSettingsList()
    return if (modeCanBeAdded(name, constellations, bands, modesList, context)) {
        ComputationSettings(
            name,
            constellations,
            bands,
            corrections,
            algorithm,
            false
        )
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
        } else { //if name is blank
            toast("Name can not be blank")
        }
    }
    return canBeAdded
}

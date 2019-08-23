package com.abecerra.gnssanalysis.core.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.abecerra.gnssanalysis.R
import kotlinx.android.synthetic.main.dialog_new_mode.view.*
import kotlinx.android.synthetic.main.dialog_settings_info.view.*

fun Context.showStopAlert(positiveAction: () -> Unit) {
    val builder = AlertDialog.Builder(this)
    with(builder) {
        setTitle(getString(R.string.alert_stop_computing_title))
        setMessage(getString(R.string.alert_stop_computing_message))
        setPositiveButton(getString(R.string.yes)) { _, _ ->
            positiveAction.invoke()
        }
        setNeutralButton(getString(R.string.cancel)) { _, _ ->
        }
        setCancelable(true)

        show()
    }
}

fun Context.showSelectedComputationSettingsAlert(positiveAction: () -> Unit) {
    val builder = AlertDialog.Builder(this)
    with(builder) {
        setTitle("Select Parameters")
        setMessage("At least one Positioning Mode must be selected")
        setPositiveButton("go to settings") { _, _ ->
            positiveAction.invoke()
        }
        setNeutralButton(getString(R.string.cancel)) { _, _ ->
        }
        setCancelable(true)

        show()
    }
}

fun Context.showNewModeDialog(onCreateSelected: (layout: View?, dialgo: AlertDialog) -> Unit) {

    val dialog = AlertDialog.Builder(this).create()
    val layout = View.inflate(this, R.layout.dialog_new_mode, null)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setView(layout)
    layout.createButton.setOnClickListener {
        onCreateSelected(layout, dialog)
    }

    layout?.let {
        // Ionosphere and iono-free can't be selected at the same time
        it.correctionsOption3.isEnabled = !it.correctionsOption1.isChecked
        it.correctionsOption1.isEnabled = !it.correctionsOption3.isChecked

        it.correctionsOption1.setOnClickListener { l ->
            it.correctionsOption3.isEnabled = !l.correctionsOption1.isChecked
        }
        it.correctionsOption3.setOnClickListener { l ->
            it.correctionsOption1.isEnabled = !l.correctionsOption3.isChecked
        }

        it.rgGpsBands.isEnabled = it.constOption1.isChecked
        it.rgGalBands.isEnabled = it.constOption2.isChecked

        it.constOption1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                it.rbL1.isEnabled = true
                it.rbL5.isEnabled = true
                it.rbL1.isChecked = true
            } else {
                it.rbL1.isEnabled = false
                it.rbL5.isEnabled = false

            }
        }

        it.constOption2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                it.rbE1.isEnabled = true
                it.rbE5a.isEnabled = true
                it.rbE1.isChecked = true
            } else {
                it.rbE1.isEnabled = false
                it.rbE5a.isEnabled = false
            }
        }


    }
    dialog.show()

}

fun Context.showInfoDialog() {

    val dialog = AlertDialog.Builder(this).create()
    val layout = View.inflate(this, R.layout.dialog_settings_info, null)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setView(layout)
    layout.ivCloseInfo.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()

}

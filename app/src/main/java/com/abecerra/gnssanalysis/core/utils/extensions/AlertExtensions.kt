package com.abecerra.gnssanalysis.core.utils.extensions

import android.app.AlertDialog
import android.content.Context
import com.abecerra.gnssanalysis.R

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

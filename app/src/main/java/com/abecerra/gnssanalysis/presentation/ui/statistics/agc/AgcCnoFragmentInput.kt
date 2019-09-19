package com.abecerra.gnssanalysis.presentation.ui.statistics.agc

import android.location.GnssMeasurement

interface AgcCnoFragmentInput {

    fun onGnssMeasurementReceived(gnssMeasurements: Collection<GnssMeasurement>)

}

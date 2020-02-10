package com.abecerra.gnssanalysis.presentation.ui.statistics.elev

import android.location.GnssStatus

interface CnoElevGraphFragmentInput {

    fun onGnssStatusReceived(gnssStatus: GnssStatus)

}

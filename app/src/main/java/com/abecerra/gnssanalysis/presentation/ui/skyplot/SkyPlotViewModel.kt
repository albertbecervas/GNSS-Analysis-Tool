package com.abecerra.gnssanalysis.presentation.ui.skyplot

import android.arch.lifecycle.MutableLiveData
import com.abecerra.gnssanalysis.core.base.BaseViewModel
import com.abecerra.gnssanalysis.core.utils.extensions.Data
import com.abecerra.gnssanalysis.core.utils.extensions.updateData
import com.abecerra.gnssanalysis.core.utils.getStatusData
import com.abecerra.gnssanalysis.presentation.data.GnssStatus
import com.abecerra.gnssanalysis.presentation.data.StatusData

class SkyPlotViewModel : BaseViewModel() {

    var status = MutableLiveData<Data<StatusData>>()

    fun obtainStatusParameters(gnssStatus: GnssStatus, selectedConstellation: SkyPlotFragment.Companion.CONSTELLATION) {
        status.updateData(getStatusData(gnssStatus, selectedConstellation))
    }

}
package com.abecerra.gnssanalysis.presentation.ui.skyplot

import androidx.lifecycle.MutableLiveData
import com.abecerra.gnssanalysis.app.base.BaseViewModel
import com.abecerra.gnssanalysis.app.utils.extensions.Data
import com.abecerra.gnssanalysis.app.utils.extensions.updateData
import com.abecerra.gnssanalysis.app.utils.getStatusData
import com.abecerra.gnssanalysis.presentation.data.StatusData
import com.abecerra.pvt_acquisition.data.GnssStatus

class SkyPlotViewModel : BaseViewModel() {

    var status = MutableLiveData<Data<StatusData>>()

    fun obtainStatusParameters(gnssStatus: GnssStatus, selectedConstellation: SkyPlotFragment.Companion.CONSTELLATION) {
        status.updateData(getStatusData(gnssStatus, selectedConstellation))
    }
}

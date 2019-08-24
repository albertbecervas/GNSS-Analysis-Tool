package com.abecerra.gnssanalysis.presentation.ui.position

import androidx.lifecycle.MutableLiveData
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseViewModel
import com.abecerra.gnssanalysis.core.computation.GnssServiceOutput
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.core.utils.context
import com.abecerra.gnssanalysis.core.utils.extensions.Data
import com.abecerra.gnssanalysis.core.utils.extensions.showError
import com.abecerra.gnssanalysis.core.utils.extensions.updateData
import com.abecerra.pvt.computation.data.ComputedPvtData

class PvtComputationViewModel : BaseViewModel(), GnssServiceOutput.PvtListener {

    val computeButtonText = MutableLiveData<Data<String>>()
    val status = MutableLiveData<Status>()

    val pvt = MutableLiveData<Data<List<ComputedPvtData>>>()

    private var isCameraIntercepted = false

    fun startComputingIfSelectedSettings() {
        if (AppSharedPreferences.getInstance().getSelectedComputationSettingsList().isEmpty()) {
            // If no constellation or band has been selected
            notifyStatusChanged(Status.NONE_PARAM_SELECTED)
        } else {
            startComputing()
        }
    }

    fun stopComputing() {
        computeButtonText.updateData(context.getString(R.string.start_computing))
    }

    fun isComputing(): Boolean = computeButtonText.value?.data == context.getString(R.string.stop_computing)

    private fun startComputing() {
        notifyStatusChanged(Status.STARTED_COMPUTING)
        computeButtonText.updateData(context.getString(R.string.stop_computing))
    }

    private fun notifyStatusChanged(newStatus: Status) {
        status.postValue(newStatus)
    }

    fun isCameraIntercepted(): Boolean = isCameraIntercepted
    fun setIsCameraIntercepted(intercepted: Boolean) {
        isCameraIntercepted = intercepted
    }

    override fun onPvtResponse(pvtResponse: List<ComputedPvtData>) {
        pvt.updateData(pvtResponse)
    }

    override fun onPvtError(error: String) {
        pvt.showError("Could not obtain position")
    }

    override fun onEphemerisObtained() {
        notifyStatusChanged(Status.HIDE_LOADING)
    }

    override fun onEphemerisError() {

    }

    enum class Status {
        STARTED_COMPUTING,
        STOPPED_COMPUTING,
        SHOW_LOADING,
        HIDE_LOADING,
        ERROR_EPH,
        ERROR_COMPUTING,
        NONE_PARAM_SELECTED
    }

}

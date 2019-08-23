package com.abecerra.gnssanalysis.presentation.ui.position

import androidx.lifecycle.MutableLiveData
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseViewModel
import com.abecerra.gnssanalysis.core.computation.GnssServiceOutput
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.core.utils.SingleLiveEvent
import com.abecerra.gnssanalysis.core.utils.context
import com.abecerra.gnssanalysis.core.utils.extensions.Data
import com.abecerra.gnssanalysis.core.utils.extensions.updateData
import timber.log.Timber

class PvtComputationViewModel : BaseViewModel(), GnssServiceOutput.PvtListener {

    val computeButtonText = MutableLiveData<Data<String>>()
    val status = SingleLiveEvent<Status>()

    fun startComputingIfSelectedSettings() {
        if (AppSharedPreferences.getInstance().getSelectedComputationSettingsList().isEmpty()) {
            // If no constellation or band has been selected
            notifyStatusChanged(Status.NONE_PARAM_SELECTED)
        } else {
            startComputing()
        }
    }

    private fun startComputing() {
        notifyStatusChanged(Status.STARTED_COMPUTING)
        computeButtonText.updateData(context.getString(R.string.stop_computing))
    }

    fun stopComputing() {
        computeButtonText.updateData(context.getString(R.string.start_computing))
    }

    fun isComputing(): Boolean = computeButtonText.value?.data == context.getString(R.string.stop_computing)

    private fun notifyStatusChanged(newStatus: Status) {
        status.value = newStatus
        status.call()
    }


    override fun onPvtResponse(pvtResponse: PvtResponse) {
        Timber.d("$PVT_COMPUTATION_TAG :::: pvtResponse received")
    }

    override fun onPvtError(error: String) {
        Timber.d("$PVT_COMPUTATION_TAG :::: pvtResponse error")
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

    companion object {
        const val PVT_COMPUTATION_TAG = "pvt_computation"
    }

}

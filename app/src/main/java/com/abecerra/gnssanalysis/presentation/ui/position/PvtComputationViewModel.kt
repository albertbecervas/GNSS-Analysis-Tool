package com.abecerra.gnssanalysis.presentation.ui.position

import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.app.base.BaseViewModel
import com.abecerra.gnssanalysis.app.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.app.utils.context
import com.abecerra.gnssanalysis.app.utils.extensions.Data
import com.abecerra.gnssanalysis.app.utils.extensions.showError
import com.abecerra.gnssanalysis.app.utils.extensions.updateData
import com.abecerra.pvt_acquisition.app.logger.GnssMeasLogger
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_acquisition.framework.GnssServiceOutput
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PvtComputationViewModel(private val gnssMeasLogger: GnssMeasLogger) : BaseViewModel(),
    GnssServiceOutput.PvtListener,
    GnssServiceOutput.GnssEventsListener {

    val computeButtonText = MutableLiveData<Data<String>>()
    val status = MutableLiveData<Status>()

    val pvt = MutableLiveData<Data<List<PvtOutputData>>>()

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
        val file = gnssMeasLogger.closeLoggerAndReturnFile()
        uploadToFirebase(file)
    }

    private fun uploadToFirebase(file: File?) {
        pvt.value?.data?.let {
            if (it.isNotEmpty() && AppSharedPreferences.getInstance().isGnssLoggingEnabled()) {
                file?.let {
                    FirebaseStorage.getInstance().reference
                        .child("nmea/${file.name}").putFile(Uri.fromFile(file))
                }
            }
        }
    }

    fun isComputing(): Boolean =
        computeButtonText.value?.data == context.getString(R.string.stop_computing)

    fun getPvtResults(): ArrayList<PvtOutputData> {
        val results = arrayListOf<PvtOutputData>()
        pvt.value?.data?.let {
            results.addAll(it)
        }
        return results
    }

    private fun startComputing() {
        notifyStatusChanged(Status.STARTED_COMPUTING)
        computeButtonText.updateData(context.getString(R.string.stop_computing))
        gnssMeasLogger.startNewLog()
    }

    private fun notifyStatusChanged(newStatus: Status) {
        status.postValue(newStatus)
    }

    fun isCameraIntercepted(): Boolean = isCameraIntercepted
    fun setIsCameraIntercepted(intercepted: Boolean) {
        isCameraIntercepted = intercepted
    }

    override fun onPvtResponse(pvtResponse: List<PvtOutputData>) {
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

    override fun onGnssStarted() {
    }

    override fun onGnssStopped() {
    }

    override fun onSatelliteStatusChanged(status: GnssStatus) {
    }

    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        gnssMeasLogger.onGnssMeasurementsReceived(event)
    }

    override fun onSensorEvent(event: SensorEvent) {
    }

    override fun onNmeaMessageReceived(message: String, timestamp: Long) {
    }

    override fun onLocationReceived(location: Location) {
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

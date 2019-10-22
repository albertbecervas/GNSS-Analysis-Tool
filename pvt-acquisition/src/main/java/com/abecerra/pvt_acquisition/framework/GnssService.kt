package com.abecerra.pvt_acquisition.framework

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.*
import android.os.Bundle
import com.abecerra.pvt_acquisition.app.base.BaseGnssService
import com.abecerra.pvt_acquisition.app.extensions.checkPermission
import com.abecerra.pvt_acquisition.app.extensions.subscribe
import com.abecerra.pvt_acquisition.domain.input.GnssServiceContract
import com.abecerra.pvt_acquisition.domain.input.GnssServiceInteractorImpl
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.domain.computation.PvtComputationInteractor
import com.abecerra.pvt_computation.domain.computation.PvtComputationInteractorImpl
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtAlgorithm
import com.abecerra.pvt_computation.domain.computation.algorithm.PvtAlgorithmImpl
import com.abecerra.pvt_ephemeris_client.suplclient.EphemerisClient
import io.reactivex.disposables.CompositeDisposable

class GnssService : BaseGnssService(), GnssServiceContract.GnssInteractorOutput,
    OnNmeaMessageListener, SensorEventListener, LocationListener {

    private var mInteractor: GnssServiceContract.GnssServiceInteractor? = null

    private var gnssStatusListener: GnssStatus.Callback? = null
    private var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback? = null

    private var referenceLocation: LlaLocation = LlaLocation()

    override fun getInstance(): GnssService = this

    override fun onCreate() {
        super.onCreate()

        setPvtComputationInteractor()
        startGnss()
    }

    private fun setPvtComputationInteractor() {
        val ephemerisClient = EphemerisClient()
        val pvtAlgorithm: PvtAlgorithm = PvtAlgorithmImpl()
        val pvtComputationInteractor: PvtComputationInteractor =
            PvtComputationInteractorImpl(pvtAlgorithm)

        mInteractor = GnssServiceInteractorImpl(ephemerisClient, pvtComputationInteractor)
        mInteractor?.bindOutput(this)
    }

    private fun startGnss() {
        if (checkPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gnssStatusListener = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    mInteractor?.setStatus(status)
                    gnssEventsListeners.forEach { it.onSatelliteStatusChanged(status) }
                }

                override fun onStarted() {
                    super.onStarted()
                    gnssEventsListeners.forEach { it.onGnssStarted() }
                }

                override fun onStopped() {
                    super.onStopped()
                    gnssEventsListeners.forEach { it.onGnssStopped() }
                }
            }

            gnssMeasurementsEventListener = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(measurementsEvent: GnssMeasurementsEvent) {
                    mInteractor?.setMeasurement(measurementsEvent)
                    gnssEventsListeners.forEach { it.onGnssMeasurementsReceived(measurementsEvent) }
                }
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                this
            )
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                this
            )

            gnssStatusListener?.let { locationManager?.registerGnssStatusCallback(it) }
            gnssMeasurementsEventListener?.let {
                locationManager?.registerGnssMeasurementsCallback(it)
            }
        }
    }

    private fun stopGnss() {
        gnssStatusListener?.let { locationManager?.unregisterGnssStatusCallback(it) }
        gnssMeasurementsEventListener?.let {
            locationManager?.unregisterGnssMeasurementsCallback(it)
        }
        locationManager?.removeUpdates(this)
    }

    fun startComputing(computationSettings: List<ComputationSettings>) {
        mInteractor?.startComputing(computationSettings, referenceLocation)?.subscribe({
            // obtaining Ephemeris
        }, {
            setNotification()
            pvtListeners.forEach { it.onEphemerisObtained() }
        }, {
            //todo check loop
            startComputing(computationSettings)
        }, CompositeDisposable())
    }

    fun stopComputing() {
        mInteractor?.stopComputing()
        stopForeground(true)
    }

    override fun onPvtResponse(pvtResponse: List<PvtOutputData>) {
        pvtListeners.forEach { it.onPvtResponse(pvtResponse) }
    }

    override fun onPvtError(error: String) {
        pvtListeners.forEach { it.onPvtError(error) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        gnssEventsListeners.forEach {
            event?.let { sensorEvent -> it.onSensorEvent(sensorEvent) }
        }
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { loc ->
            referenceLocation = LlaLocation(loc.latitude, loc.altitude, loc.altitude)
            gnssEventsListeners.forEach {
                it.onLocationReceived(loc)
            }
        }
    }

    override fun onNmeaMessage(message: String?, timestamp: Long) {
        gnssEventsListeners.forEach {
            message?.let { msg -> it.onNmeaMessageReceived(msg, timestamp) }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    override fun onDestroy() {
        super.onDestroy()
        stopGnss()
    }

    companion object {
        private const val MIN_TIME = 1L
        private const val MIN_DISTANCE = 0.0F
    }
}

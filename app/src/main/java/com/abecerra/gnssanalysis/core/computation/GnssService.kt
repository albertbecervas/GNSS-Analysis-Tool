package com.abecerra.gnssanalysis.core.computation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.*
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.computation.presenter.PvtPresenterImpl
import com.abecerra.gnssanalysis.core.computation.presenter.PvtServiceContract
import com.abecerra.gnssanalysis.core.logger.GnssMeasLogger
import com.abecerra.gnssanalysis.core.utils.NotificationBuilder.buildNotification
import com.abecerra.gnssanalysis.core.utils.extensions.checkPermission
import com.abecerra.gnssanalysis.core.utils.extensions.context
import com.abecerra.gnssanalysis.core.utils.extensions.subscribe
import com.abecerra.pvt.computation.data.ComputationSettings
import io.reactivex.disposables.CompositeDisposable

class GnssService : Service(), PvtServiceContract.PvtPresenterOutput, OnNmeaMessageListener, SensorEventListener,
    LocationListener {

    private var mPresenter: PvtServiceContract.PvtPresenter? = null

    private var gnssStatusListener: GnssStatus.Callback? = null
    private var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback? = null
    private var locationManager: LocationManager? = null

    private val mBinder = PvtServiceBinder()

    private val pvtListeners = arrayListOf<GnssServiceOutput.PvtListener>()
    private val gnssEventsListeners = arrayListOf<GnssServiceOutput.GnssEventsListener>()

    private var gnssMeasLogger: GnssMeasLogger? = null

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        mPresenter = PvtPresenterImpl(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gnssMeasLogger = GnssMeasLogger()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startGnss()
        return START_STICKY
    }

    private fun setNotification() {
        val notification = context.buildNotification()
        startForeground(1, notification)
    }

    fun bindPvtListener(pvtListener: GnssServiceOutput.PvtListener) {
        if (pvtListeners.contains(pvtListener)) {
            pvtListeners.remove(pvtListener)
        }
        pvtListeners.add(pvtListener)
    }

    fun unbindPvtListener(pvtListener: GnssServiceOutput.PvtListener) {
        if (pvtListeners.contains(pvtListener)) {
            pvtListeners.remove(pvtListener)
        }
    }

    fun bindGnssEventsListener(gnssEventsListener: GnssServiceOutput.GnssEventsListener) {
        if (gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.remove(gnssEventsListener)
        }
        gnssEventsListeners.add(gnssEventsListener)
    }

    fun unbindGnssEventsListener(gnssEventsListener: GnssServiceOutput.GnssEventsListener) {
        if (gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.remove(gnssEventsListener)
        }
    }


    private fun startGnss() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gnssStatusListener = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    mPresenter?.setStatus(status)
                    gnssEventsListeners.forEach {
                        it.onSatelliteStatusChanged(status)
                    }
                }

                override fun onStarted() {
                    super.onStarted()
                    gnssEventsListeners.forEach {
                        it.onGnssStarted()
                    }
                }

                override fun onStopped() {
                    super.onStopped()
                    gnssEventsListeners.forEach {
                        it.onGnssStopped()
                    }
                }
            }

            gnssMeasurementsEventListener = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(measurementsEvent: GnssMeasurementsEvent) {
                    mPresenter?.setMeasurement(measurementsEvent)
                    gnssMeasLogger?.onGnssMeasurementsReceived(measurementsEvent)
                    gnssEventsListeners.forEach {
                        it.onGnssMeasurementsReceived(measurementsEvent)
                    }
                }
            }

            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this)
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this)

            locationManager?.registerGnssStatusCallback(gnssStatusListener)
            locationManager?.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        }
    }

    private fun stopGnss() {
        locationManager?.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager?.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
        locationManager?.removeUpdates(this)
    }


    fun startComputing(computationSettings: List<ComputationSettings>) {
        mPresenter?.startComputing(computationSettings)?.subscribe({
            // obtaining Ephemeris
        }, {
            setNotification()
            startGnss()
        }, {
            //todo check loop
//            startComputing(computationSettings)
            startGnss()
        }, CompositeDisposable())
    }

    fun stopComputing() {
        mPresenter?.stopComputing()
        stopForeground(true)
    }

    override fun onPvtResponse(pvtResponse: PvtResponse) {
        pvtListeners.forEach { it.onPvtResponse(pvtResponse) }
    }

    override fun onPvtError(error: String) {
        pvtListeners.forEach { it.onPvtError(error) }
    }

    override fun onNmeaMessage(message: String?, timestamp: Long) {
        gnssEventsListeners.forEach {
            message?.let { msg ->
                it.onNmeaMessageReceived(msg, timestamp)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        gnssEventsListeners.forEach {
            event?.let { sensorEvent ->
                it.onSensorEvent(sensorEvent)
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        gnssEventsListeners.forEach {
            location?.let { loc ->
                it.onLocationReceived(loc)
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //no-op
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //no-op
    }

    override fun onProviderEnabled(provider: String?) {
        //no-op
    }

    override fun onProviderDisabled(provider: String?) {
        //no-op
    }

    override fun onDestroy() {
        stopGnss()
        super.onDestroy()
    }

    inner class PvtServiceBinder : Binder() {
        val service: GnssService
            get() = this@GnssService
    }


    interface GnssServiceOutput {

        interface GnssEventsListener {

            fun onGnssStarted()

            fun onGnssStopped()

            fun onSatelliteStatusChanged(status: GnssStatus)

            fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent)

            fun onSensorEvent(event: SensorEvent)

            fun onNmeaMessageReceived(message: String, timestamp: Long)

            fun onLocationReceived(location: Location)

        }

        interface PvtListener {

            fun onPvtResponse(pvtResponse: PvtResponse)

            fun onPvtError(error: String)

        }
    }

    companion object {
        private const val MIN_TIME = 1L
        private const val MIN_DISTANCE = 0.0F
    }

}
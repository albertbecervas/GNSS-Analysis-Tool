package com.abecerra.gnssanalysis.core.computation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.*
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.computation.presenter.PvtPresenter
import com.abecerra.gnssanalysis.core.computation.presenter.PvtPresenterImpl
import com.abecerra.gnssanalysis.core.logger.GnssMeasLogger
import com.abecerra.gnssanalysis.core.utils.NotificationBuilder.buildNotification
import com.abecerra.gnssanalysis.core.utils.extensions.checkPermission
import com.abecerra.gnssanalysis.core.utils.extensions.context
import com.abecerra.gnssanalysis.core.utils.extensions.subscribe
import com.abecerra.pvt.computation.data.ComputationSettings
import io.reactivex.disposables.CompositeDisposable
import java.io.Serializable

class PvtService : Service(), LocationListener, OnNmeaMessageListener, SensorEventListener, PvtPresenter.PvtListener {

    private var mPresenter: PvtPresenter? = null

    private var gnssStatusListener: GnssStatus.Callback? = null
    private var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback? = null
    private var locationManager: LocationManager? = null

    private var broadcaster: LocalBroadcastManager? = null

    private val mBinder = PvtServiceBinder()

    private var gnssMeasLogger: GnssMeasLogger? = null

    inner class PvtServiceBinder : Binder() {
        val service: PvtService
            get() = this@PvtService
    }


    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        mPresenter = PvtPresenterImpl(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gnssMeasLogger = GnssMeasLogger()


        if (broadcaster == null) {
            broadcaster = LocalBroadcastManager.getInstance(this@PvtService)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startGnss()

        return START_STICKY
    }

    private fun setNotification() {
        val notification = context.buildNotification()
        startForeground(1, notification)
    }

    private fun startGnss() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gnssStatusListener = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    mPresenter?.setStatus(status)
                }

                override fun onStarted() {
                    super.onStarted()
                    sendBroadcast(BROADCAST_START_GNSS)
                }

                override fun onStopped() {
                    super.onStopped()
                    sendBroadcast(BROADCAST_STOP_GNSS)
                }
            }

            gnssMeasurementsEventListener = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(measurementsEvent: GnssMeasurementsEvent) {
                    mPresenter?.setMeasurement(measurementsEvent)
                    gnssMeasLogger?.onGnssMeasurementsReceived(measurementsEvent)
                }
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE, this
            )
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE, this
            )

            locationManager?.registerGnssStatusCallback(gnssStatusListener)
            locationManager?.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
        val extrasMap = hashMapOf<String, Serializable>()
        extrasMap[PVT_RESULT_EXTRA] = pvtResponse
        sendBroadcast(BROADCAST_PVT_RESULT, extrasMap)
    }

    override fun onPvtError(error: String) {

    }

    private fun sendBroadcast(action: String, extras: HashMap<String, Serializable> = hashMapOf()) {
        val intent = Intent()
        intent.action = action
        extras.forEach {
            intent.putExtra(it.key, it.value)
        }
        broadcaster?.sendBroadcast(intent)
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun onNmeaMessage(message: String?, timestamp: Long) {

    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopGnss()
        }
        super.onDestroy()
    }

    companion object {
        private const val MIN_TIME = 1L
        private const val MIN_DISTANCE = 0.0F

        const val BROADCAST_PVT_RESULT: String = ".core.services.PvtService.BROADCAST_PVT_RESULT"
        const val BROADCAST_START_GNSS: String = ".core.services.PvtService.BROADCAST_START_GNSS"
        const val BROADCAST_STOP_GNSS: String = ".core.services.PvtService.BROADCAST_STOP_GNSS"

        const val PVT_RESULT_EXTRA: String = ".core.services.PvtService.PVT_RESULT_EXTRA"
    }

}
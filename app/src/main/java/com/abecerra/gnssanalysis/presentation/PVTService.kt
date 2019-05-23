package com.abecerra.gnssanalysis.presentation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.*
import android.os.*
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.abecerra.gnssanalysis.core.utils.extensions.checkPermission
import com.abecerra.pvt.computation.PVTEngine
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.ephemeris.EphemerisClient


class PVTService : Service(), LocationListener, SensorEventListener {

    private var pvtEngine = PVTEngine()
    private var ephemerisClient = EphemerisClient()

    private var locationManager: LocationManager? = null

    private var gnssStatusListener: GnssStatus.Callback? = null
    private var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback? = null
    private var gnssNmeaMessageListener: OnNmeaMessageListener? = null

    private var isComputingPVT = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startGnss()
        }

        startComputing()

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startGnss() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gnssStatusListener = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                }

                override fun onStarted() {
                    super.onStarted()

                }

                override fun onStopped() {
                    super.onStopped()
                }
            }

            gnssMeasurementsEventListener = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(measurementsEvent: GnssMeasurementsEvent?) {
                    if (isComputingPVT) {
                        val position = pvtEngine.obtainPosition()
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            Toast.makeText(
                                this@PVTService.applicationContext,
                                "computed pos = ${position.llaLocation}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }

            gnssNmeaMessageListener = OnNmeaMessageListener { message, timestamp ->

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
            locationManager?.addNmeaListener(gnssNmeaMessageListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopGnss() {
        locationManager?.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager?.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
        locationManager?.removeNmeaListener(gnssNmeaMessageListener)

        locationManager?.removeUpdates(this)
    }


    fun startComputing() {
        ephemerisClient.getEphemerisData(LlaLocation(), {
            isComputingPVT = true
        }, {

        })
    }

    fun stopComputing() {
        isComputingPVT = false
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

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopGnss()
        }
        super.onDestroy()
    }

    companion object {
        private const val MIN_TIME = 1L
        private const val MIN_DISTANCE = 0.0F
    }

}
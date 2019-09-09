package com.abecerra.gnssanalysis.core.base

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.gnssanalysis.core.computation.GnssServiceOutput
import com.abecerra.gnssanalysis.core.utils.NotificationBuilder.buildGnssNotification

abstract class BaseGnssService : Service() {

    protected val pvtListeners = arrayListOf<GnssServiceOutput.PvtListener>()
    protected val gnssEventsListeners = arrayListOf<GnssServiceOutput.GnssEventsListener>()

    protected var locationManager: LocationManager? = null
    private var mSensorManager: SensorManager? = null

    private val mBinder = PvtServiceBinder()

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    fun bindPvtListener(pvtListener: GnssServiceOutput.PvtListener) {
        if (!pvtListeners.contains(pvtListener)) {
            pvtListeners.add(pvtListener)
        }
    }

    fun unbindPvtListener(pvtListener: GnssServiceOutput.PvtListener) {
        if (pvtListeners.contains(pvtListener)) {
            pvtListeners.remove(pvtListener)
        }
    }

    fun bindGnssEventsListener(gnssEventsListener: GnssServiceOutput.GnssEventsListener) {
        if (!gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.add(gnssEventsListener)
        }
    }

    fun unbindGnssEventsListener(gnssEventsListener: GnssServiceOutput.GnssEventsListener) {
        if (gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.remove(gnssEventsListener)
        }
    }

    protected fun setNotification() {
        startForeground(1, buildGnssNotification())
    }

    inner class PvtServiceBinder : Binder() {
        val service: GnssService
            get() = getInstance()
    }

    abstract fun getInstance(): GnssService
}
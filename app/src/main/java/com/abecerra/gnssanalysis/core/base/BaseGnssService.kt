package com.abecerra.gnssanalysis.core.base

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.gnssanalysis.core.utils.NotificationBuilder.buildNotification
import com.abecerra.gnssanalysis.core.utils.extensions.context

abstract class BaseGnssService : Service() {

    protected val pvtListeners = arrayListOf<GnssService.GnssServiceOutput.PvtListener>()
    protected val gnssEventsListeners = arrayListOf<GnssService.GnssServiceOutput.GnssEventsListener>()

    protected var locationManager: LocationManager? = null
    private var mSensorManager: SensorManager? = null

    private val mBinder = PvtServiceBinder()

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun bindPvtListener(pvtListener: GnssService.GnssServiceOutput.PvtListener) {
        if (!pvtListeners.contains(pvtListener)) {
            pvtListeners.add(pvtListener)
        }
    }

    fun unbindPvtListener(pvtListener: GnssService.GnssServiceOutput.PvtListener) {
        if (pvtListeners.contains(pvtListener)) {
            pvtListeners.remove(pvtListener)
        }
    }

    fun bindGnssEventsListener(gnssEventsListener: GnssService.GnssServiceOutput.GnssEventsListener) {
        if (!gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.add(gnssEventsListener)
        }
    }

    fun unbindGnssEventsListener(gnssEventsListener: GnssService.GnssServiceOutput.GnssEventsListener) {
        if (gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.remove(gnssEventsListener)
        }
    }

    protected fun setNotification() {
        val notification = context.buildNotification()
        startForeground(1, notification)
    }

    inner class PvtServiceBinder : Binder() {
        val service: GnssService
            get() = getInstance()
    }

    abstract fun getInstance(): GnssService
}
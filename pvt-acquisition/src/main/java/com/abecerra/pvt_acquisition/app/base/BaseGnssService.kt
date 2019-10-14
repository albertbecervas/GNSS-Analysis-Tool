package com.abecerra.pvt_acquisition.app.base

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import com.abecerra.pvt_acquisition.framework.GnssService
import com.abecerra.pvt_acquisition.framework.GnssServiceOutput
import com.abecerra.pvt_acquisition.app.NotificationBuilder.buildGnssNotification

abstract class BaseGnssService : Service() {

    protected val pvtListeners = arrayListOf<GnssServiceOutput.PvtListener>()
    protected val gnssEventsListeners = arrayListOf<GnssServiceOutput.GnssEventsListener>()

    protected var locationManager: LocationManager? = null
    private var mSensorManager: SensorManager? = null

    private val mBinder = PvtServiceBinder()

    private var notificationPendingIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    fun setNotificationPedingIntent(pendingIntent: PendingIntent) {
        notificationPendingIntent = pendingIntent
    }

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

    fun bindGnssEventsListeners(gnssEventsListeners: List<GnssServiceOutput.GnssEventsListener>) {
        this.gnssEventsListeners.clear()
        this.gnssEventsListeners.addAll(gnssEventsListeners)
    }

    fun unbindGnssEventsListener(gnssEventsListener: GnssServiceOutput.GnssEventsListener) {
        if (gnssEventsListeners.contains(gnssEventsListener)) {
            gnssEventsListeners.remove(gnssEventsListener)
        }
    }

    fun unbindGnssEventsListeners() {
        this.gnssEventsListeners.clear()
    }

    protected fun setNotification() {
        buildGnssNotification(notificationPendingIntent)?.let {
            startForeground(NOTIFICATION_ID, it)
        }
    }

    inner class PvtServiceBinder : Binder() {
        val service: GnssService
            get() = getInstance()
    }

    abstract fun getInstance(): GnssService

    companion object {
        const val NOTIFICATION_ID: Int = 1
    }
}

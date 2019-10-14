package com.abecerra.pvt_acquisition.app.base

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.abecerra.pvt_acquisition.framework.GnssService
import com.abecerra.pvt_acquisition.framework.GnssServiceOutput

/**
 * This BaseGnssActivity creates the GnssService and implements the connection with it.
 */
abstract class BaseGnssActivity : AppCompatActivity() {

    protected var mService: GnssService? = null

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as? BaseGnssService.PvtServiceBinder
            binder?.service?.let {
                mService = it
                this@BaseGnssActivity.onGnssServiceConnected()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            this@BaseGnssActivity.onGnssServiceDisconnected()
            mService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startGnssService()
    }

    private fun startGnssService() {
        val intent = Intent(this, GnssService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopGnssService() {
        mService?.let { unbindService(mConnection) }
    }

    /**
     * This onGnssServiceConnected function is used to bind listeners to the service once Binder is connected.
     *
     * @param service GnssService not null instance used to bind the listeners.
     */
    protected fun onGnssServiceConnected() {
        mService?.setNotificationPedingIntent(getNotificationPendingIntent())
        mService?.bindGnssEventsListeners(getActiveListeners())
    }

    /**
     * This onGnssServiceDisconnected function is used to notify that Binder is disconnected.
     */
    protected fun onGnssServiceDisconnected() {
        mService?.unbindGnssEventsListeners()
    }

    /**
     * This getActiveListeners function is used to retrieve all GnssEventsListeners connected.
     */
    abstract fun getActiveListeners(): List<GnssServiceOutput.GnssEventsListener>

    /**
     * This getNotificationPendingIntent is used to retrieve the intent that will be used for
     * GnssService displayed notification.
     */
    abstract fun getNotificationPendingIntent(): PendingIntent
}

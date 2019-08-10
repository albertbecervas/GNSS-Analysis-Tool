package com.abecerra.gnssanalysis.core.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.pvt.computation.data.ComputationSettings


abstract class BaseGnssFragment : BaseFragment() {


    protected var mService: GnssService? = null

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as GnssService.PvtServiceBinder
            mService = binder.service
            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startGnssService()
    }

    abstract fun onServiceConnected()

    private fun startGnssService() {
        context?.let { c ->
            val intent = Intent(c, GnssService::class.java)
            c.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun startComputing(computationSettings: List<ComputationSettings>) {
        mService?.startComputing(computationSettings)
    }

    fun stopComputing() {
        mService?.stopComputing()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mService?.let {
//            context?.unbindService(mConnection)
//        }
//    }

}
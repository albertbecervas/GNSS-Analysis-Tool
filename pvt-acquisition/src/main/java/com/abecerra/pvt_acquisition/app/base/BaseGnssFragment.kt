package com.abecerra.pvt_acquisition.app.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import com.abecerra.pvt_acquisition.framework.GnssService

abstract class BaseGnssFragment : Fragment() {

    protected var mService: GnssService? = null

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder =
                service as com.abecerra.pvt_acquisition.app.base.BaseGnssService.PvtServiceBinder
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
}

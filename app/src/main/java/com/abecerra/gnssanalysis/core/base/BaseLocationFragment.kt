package com.abecerra.gnssanalysis.core.base

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.abecerra.gnssanalysis.core.computation.PvtService
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.pvt.computation.data.ComputationSettings


abstract class BaseLocationFragment : BaseFragment() {

    private var mService: PvtService? = null

    private var localBroadcastManager: LocalBroadcastManager? = null
    private var messageReceiver: BroadcastReceiver? = null

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PvtService.PvtServiceBinder
            mService = binder.service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService = null
        }
    }

    protected abstract fun onPvtResult(pvtResponse: PvtResponse?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let { c ->
            val intent = Intent(c, PvtService::class.java)
            c.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }

        setBroadcastReceiver()
    }

    private fun setBroadcastReceiver() {
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PvtService.BROADCAST_PVT_RESULT -> {
                        val pvtResult = intent.getSerializableExtra(PvtService.PVT_RESULT_EXTRA) as? PvtResponse
                        onPvtResult(pvtResult)
                    }
                }
            }
        }

        context?.let { c ->
            LocalBroadcastManager.getInstance(c).let { broadcastManager ->
                localBroadcastManager = broadcastManager
                val intentFilter = IntentFilter()
                intentFilter.addAction(PvtService.BROADCAST_PVT_RESULT)
                broadcastManager.registerReceiver(messageReceiver as BroadcastReceiver, intentFilter)
            }
        }
    }

    fun startComputing(computationSettings: List<ComputationSettings>) {
        mService?.startComputing(computationSettings)
    }

    fun stopComputing() {
        mService?.stopComputing()
    }

    override fun onStop() {
        super.onStop()
        mService?.let {
            context?.unbindService(mConnection)
        }
    }

}
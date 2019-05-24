package com.abecerra.gnssanalysis.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity
import com.abecerra.gnssanalysis.core.services.PVTService
import com.abecerra.pvt.computation.data.ComputedPositionData
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : BaseActivity() {

    private var localBroadcastManager: LocalBroadcastManager? = null
    private var messageReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btStart.setOnClickListener {

            setBroadcastReceiver()

            val i = Intent(this, PVTService::class.java)
            i.putExtra("KEY1", "Value to be used by the service")
            ContextCompat.startForegroundService(this, i)
        }

        btStop.setOnClickListener {
            stopService(Intent(this, PVTService::class.java))
            messageReceiver?.let { localBroadcastManager?.unregisterReceiver(it) }
        }

    }

    private fun setBroadcastReceiver() {
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    PVTService.SEND_PVT_RESULT -> {
                        val pvtResult =
                            intent.getSerializableExtra(PVTService.PVT_RESULT_EXTRA) as? ComputedPositionData
                        onPVTResult(pvtResult)
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this).let {
            localBroadcastManager = it
            val intentFilter = IntentFilter()
            intentFilter.addAction(PVTService.SEND_PVT_RESULT)
            it.registerReceiver(messageReceiver as BroadcastReceiver, intentFilter)
        }
    }

    private fun onPVTResult(computedPositionData: ComputedPositionData?) {
        computedPositionData?.let {
            Timber.d("PVT Result: ${it.llaLocation}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, PVTService::class.java))
        messageReceiver?.let { localBroadcastManager?.unregisterReceiver(it) }
    }

}

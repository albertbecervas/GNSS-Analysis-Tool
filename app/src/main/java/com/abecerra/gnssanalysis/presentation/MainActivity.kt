package com.abecerra.gnssanalysis.presentation

import android.content.Intent
import android.os.Bundle
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val i = Intent(this, PVTService::class.java)
        i.putExtra("KEY1", "Value to be used by the service")
        startService(i)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, PVTService::class.java))
    }

}

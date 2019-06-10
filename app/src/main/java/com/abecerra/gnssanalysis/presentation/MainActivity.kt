package com.abecerra.gnssanalysis.presentation

import android.os.Bundle
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity
import com.abecerra.gnssanalysis.presentation.position.PositionFragment


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.flMap, PositionFragment())
            .commit()

    }

}

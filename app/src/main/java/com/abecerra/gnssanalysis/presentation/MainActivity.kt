package com.abecerra.gnssanalysis.presentation

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity
import com.abecerra.pvt.suplclient.ephemeris.EphemerisResponse
import com.abecerra.pvt.suplclient.supl.SuplConnectionRequest
import com.abecerra.pvt.suplclient.supl.SuplController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToLong


class MainActivity : BaseActivity() {

    private var suplController: SuplController? = null
    private var refPos: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refPos = LatLng(47.53, 2.11)

        buildSuplController()

        obtainEphemerisData()

    }

    private fun buildSuplController() {
        val request = SuplConnectionRequest()
        request.serverHost = SUPL_SERVER_HOST
        request.serverPort = SUPL_SERVER_PORT
        request.isSslEnabled = SUPL_SSL_ENABLED
        request.isMessageLoggingEnabled =
            SUPL_MESSAGE_LOGGING_ENABLED
        request.isLoggingEnabled = SUPL_LOGGING_ENABLED
        suplController = SuplController(request)
    }

    private fun obtainEphemerisData() {
        GlobalScope.launch {
            var ephResponse: EphemerisResponse? = null
            refPos?.let {
                val latE7 = (it.latitude * 1e7).roundToLong()
                val lngE7 = (it.longitude * 1e7).roundToLong()

                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
                suplController?.sendSuplRequest(latE7, lngE7)
                ephResponse = suplController?.generateEphResponse(latE7, lngE7)
                ephResponse?.let {
                } ?: kotlin.run {
                }

            }
        }
    }

    companion object {
        const val SUPL_SERVER_HOST = "supl.google.com"
        const val SUPL_SERVER_PORT = 7275
        const val SUPL_SSL_ENABLED = true
        const val SUPL_MESSAGE_LOGGING_ENABLED = true
        const val SUPL_LOGGING_ENABLED = true
    }

}

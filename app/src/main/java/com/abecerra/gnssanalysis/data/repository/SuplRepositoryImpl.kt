package com.abecerra.gnssanalysis.data.repository

import android.os.StrictMode
import com.abecerra.gnssanalysis.MainActivity
import com.abecerra.gnssanalysis.core.suplclient.ephemeris.EphemerisResponse
import com.abecerra.gnssanalysis.core.suplclient.supl.SuplConnectionRequest
import com.abecerra.gnssanalysis.core.suplclient.supl.SuplController
import com.abecerra.gnssanalysis.domain.repository.SuplRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class SuplRepositoryImpl : SuplRepository {

    private var suplController: SuplController? = null

    init {
        buildSuplController()
    }

    private fun buildSuplController() {
        val request = SuplConnectionRequest()
        request.serverHost = MainActivity.SUPL_SERVER_HOST
        request.serverPort = MainActivity.SUPL_SERVER_PORT
        request.isSslEnabled = MainActivity.SUPL_SSL_ENABLED
        request.isMessageLoggingEnabled = MainActivity.SUPL_MESSAGE_LOGGING_ENABLED
        request.isLoggingEnabled = MainActivity.SUPL_LOGGING_ENABLED
        suplController = SuplController(request)
    }

    override fun getEphemerisData(refPos: LatLng) {
        GlobalScope.launch {
            var ephResponse: EphemerisResponse? = null
            val latE7 = (refPos.latitude * 1e7).roundToLong()
            val lngE7 = (refPos.longitude * 1e7).roundToLong()

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
            suplController?.sendSuplRequest(latE7, lngE7)
            ephResponse = suplController?.generateEphResponse(latE7, lngE7)

        }
    }

}
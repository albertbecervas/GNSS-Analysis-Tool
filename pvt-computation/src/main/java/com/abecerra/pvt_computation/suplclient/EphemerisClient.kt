package com.abecerra.pvt_computation.suplclient

import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.suplclient.ephemeris.EphemerisResponse
import com.abecerra.pvt_computation.suplclient.supl.SuplConnectionRequest
import com.abecerra.pvt_computation.suplclient.supl.SuplController
import io.reactivex.Single
import kotlin.math.roundToLong

class EphemerisClient {

    private var suplController: SuplController? = null

    init {
        buildSuplController()
    }

    private fun buildSuplController() {
        val request = SuplConnectionRequest()
        request.serverHost =
            SUPL_SERVER_HOST
        request.serverPort =
            SUPL_SERVER_PORT
        request.isSslEnabled =
            SUPL_SSL_ENABLED
        request.isMessageLoggingEnabled =
            SUPL_MESSAGE_LOGGING_ENABLED
        request.isLoggingEnabled =
            SUPL_LOGGING_ENABLED
        suplController = SuplController(request)
    }

    fun getEphemerisData(
        refPos: LlaLocation
    ): Single<EphemerisResponse> {

        return Single.create { emitter ->
            val ephResponse: EphemerisResponse?
            val latE7 = (refPos.latitude * 1e7).roundToLong()
            val lngE7 = (refPos.longitude * 1e7).roundToLong()

            suplController?.sendSuplRequest(latE7, lngE7)
            ephResponse = suplController?.generateEphResponse(latE7, lngE7)
            ephResponse?.let {
                emitter.onSuccess(it)
            } ?: run {
                emitter.onError(Throwable("error obtaining ephemeris"))
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

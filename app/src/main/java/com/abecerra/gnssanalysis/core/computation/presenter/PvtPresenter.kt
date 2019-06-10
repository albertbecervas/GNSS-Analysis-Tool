package com.abecerra.gnssanalysis.core.computation.presenter

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.pvt.computation.data.ComputationSettings
import io.reactivex.Single

interface PvtPresenter {

    fun startComputing(computationSettings: List<ComputationSettings>): Single<Any>
    fun stopComputing()

    fun setStatus(gnssStatus: GnssStatus)
    fun setMeasurement(measurementsEvent: GnssMeasurementsEvent)

    interface PvtListener {
        fun onPvtResponse(pvtResponse: PvtResponse)
        fun onPvtError(error: String)
    }
}
package com.abecerra.gnssanalysis.core.computation.presenter

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.ComputedPvtData
import io.reactivex.Single

interface PvtServiceContract {

    interface PvtPresenter {

        fun bindOutput(output: PvtPresenterOutput)

        fun startComputing(computationSettings: List<ComputationSettings>): Single<String>
        fun stopComputing()

        fun setStatus(gnssStatus: GnssStatus)
        fun setMeasurement(measurementsEvent: GnssMeasurementsEvent)
    }

    interface PvtPresenterOutput {
        fun onPvtResponse(pvtResponse: List<ComputedPvtData>)
        fun onPvtError(error: String)
    }
}
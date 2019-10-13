package com.abecerra.pvt_acquisition.acquisition.presenter

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.output.PvtOutputData
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
        fun onPvtResponse(pvtResponse: List<PvtOutputData>)
        fun onPvtError(error: String)
    }
}

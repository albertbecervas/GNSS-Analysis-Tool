package com.abecerra.pvt_acquisition.domain.input

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.output.PvtOutputData
import io.reactivex.Single

interface GnssServiceContract {

    interface GnssServiceInteractor {

        fun bindOutput(output: GnssInteractorOutput)

        fun startComputing(computationSettings: List<ComputationSettings>): Single<String>
        fun stopComputing()

        fun setStatus(gnssStatus: GnssStatus)
        fun setMeasurement(measurementsEvent: GnssMeasurementsEvent)
    }

    interface GnssInteractorOutput {
        fun onPvtResponse(pvtResponse: List<PvtOutputData>)
        fun onPvtError(error: String)
    }
}

package com.abecerra.pvt_acquisition.acquisition.presenter

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.PvtEngine
import com.abecerra.pvt_computation.domain.computation.data.LlaLocation
import com.abecerra.pvt_computation.domain.computation.ephemeris.EphemerisClient
import com.abecerra.pvt_acquisition.app.extensions.subscribe
import com.abecerra.pvt_acquisition.data.parser.EpochDataParser
import com.abecerra.pvt_acquisition.data.GnssComputationData
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class PvtPresenterImpl : PvtServiceContract.PvtPresenter {

    private var mListener: PvtServiceContract.PvtPresenterOutput? = null

    private val ephemerisClient: EphemerisClient = EphemerisClient()

    private var gnssComputationData = GnssComputationData()

    override fun bindOutput(output: PvtServiceContract.PvtPresenterOutput) {
        this.mListener = output
    }

    override fun startComputing(computationSettings: List<ComputationSettings>): Single<String> {
        return Single.create { emitter ->
            ephemerisClient.getEphemerisData(LlaLocation())
                .subscribe({
                }, {
                    gnssComputationData.ephemerisResponse = it
                    gnssComputationData.startedComputingDate = Date()
                    gnssComputationData.computationSettings = computationSettings
                    emitter.onSuccess("")
                }, {
                    emitter.onError(Throwable())
                }, CompositeDisposable())
        }
    }

    override fun stopComputing() {
        gnssComputationData = GnssComputationData()
    }

    override fun setStatus(gnssStatus: GnssStatus) {
        gnssComputationData.gnssStatus = gnssStatus
    }

    override fun setMeasurement(measurementsEvent: GnssMeasurementsEvent) {
        gnssComputationData.ephemerisResponse?.let { ephemeris ->
            gnssComputationData.gnssStatus?.let { status ->
                val epoch = EpochDataParser.parseEpoch(measurementsEvent, status, ephemeris)
                computePvt(epoch)
            }
        }
    }

    private fun computePvt(epoch: Epoch) {

        gnssComputationData.epochs.add(epoch)

        if (isMeanTimePassed()) {
            val gnssData = gnssComputationData.parseToGnssData()
            PvtEngine.computePosition(gnssData)
                .subscribe({
                    // Computing PVT
                }, { computedPvtData ->
                    mListener?.onPvtResponse(computedPvtData)
                }, {
                    // Error computing PVT
                    mListener?.onPvtError("")
                }, CompositeDisposable())
        }
    }

    private fun isMeanTimePassed() =
        Date().time - gnssComputationData.startedComputingDate.time > 1L
}

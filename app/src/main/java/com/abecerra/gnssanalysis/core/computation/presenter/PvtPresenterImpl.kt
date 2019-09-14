package com.abecerra.gnssanalysis.core.computation.presenter

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.gnssanalysis.core.computation.EpochDataParser
import com.abecerra.gnssanalysis.core.computation.data.GnssComputationData
import com.abecerra.gnssanalysis.core.computation.repository.PvtRepository
import com.abecerra.gnssanalysis.core.logger.GnssMeasLogger
import com.abecerra.gnssanalysis.core.utils.extensions.subscribe
import com.abecerra.pvt.computation.PvtEngine
import com.abecerra.pvt.computation.data.ComputationSettings
import com.abecerra.pvt.computation.data.Epoch
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.ephemeris.EphemerisClient
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.Date

class PvtPresenterImpl(private val gnssMeasLogger: GnssMeasLogger, private val pvtRepository: PvtRepository) :
    PvtServiceContract.PvtPresenter {

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
                    gnssMeasLogger.startNewLog()
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
        gnssMeasLogger.closeLoggerAndReturnFile()?.let {
            pvtRepository.uploadNmeaFile(it)
        }
        gnssComputationData = GnssComputationData()
    }

    override fun setStatus(gnssStatus: GnssStatus) {
        gnssComputationData.gnssStatus = gnssStatus
    }

    override fun setMeasurement(measurementsEvent: GnssMeasurementsEvent) {
        gnssMeasLogger.onGnssMeasurementsReceived(measurementsEvent)
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

    private fun isMeanTimePassed() = Date().time - gnssComputationData.startedComputingDate.time > 1L
}

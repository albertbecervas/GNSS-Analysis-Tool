package com.abecerra.pvt_acquisition.domain.input

import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import com.abecerra.pvt_acquisition.app.extensions.subscribe
import com.abecerra.pvt_acquisition.data.GnssComputationData
import com.abecerra.pvt_acquisition.data.mapper.PvtInputDataMapper
import com.abecerra.pvt_acquisition.domain.acquisition.EpochAcquisitionDataBuilder
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.Location
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.domain.computation.PvtComputationInteractor
import com.abecerra.pvt_computation.domain.computation.utils.CoordinatesConverter.lla2ecef
import com.abecerra.pvt_acquisition.app.utils.Logger
import com.abecerra.pvt_acquisition.data.inari.GnssData
import com.abecerra.pvt_acquisition.data.inari.MeasurementData
import com.abecerra.pvt_acquisition.data.inari.ModeParser
import com.abecerra.pvt_acquisition.data.inari.RefLocation
import com.abecerra.pvt_ephemeris_client.suplclient.EphemerisClient
import com.abecerra.pvt_ephemeris_client.suplclient.data.EphLocation
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class GnssServiceInteractorImpl(
    private val ephemerisClient: EphemerisClient,
    private val pvtComputationInteractor: PvtComputationInteractor
) : GnssServiceContract.GnssServiceInteractor {

    private var mListener: GnssServiceContract.GnssInteractorOutput? = null

    private var gnssComputationData = GnssComputationData()

    //Todo remove after pvt algorithm matches Inari algorithm.
    private var gnssData = GnssData()

    override fun bindOutput(output: GnssServiceContract.GnssInteractorOutput) {
        this.mListener = output
    }

    override fun startComputing(
        computationSettings: List<ComputationSettings>, referenceLocation: LlaLocation
    ): Single<String> {

        gnssComputationData.refLocation = Location(referenceLocation, lla2ecef(referenceLocation))

        gnssData.location = RefLocation(
            referenceLocation.latitude,
            referenceLocation.longitude, referenceLocation.altitude
        )

        return Single.create { emitter ->
            ephemerisClient.getEphemerisData(
                EphLocation(referenceLocation.latitude, referenceLocation.longitude)
            ).subscribe({
            }, {
                gnssComputationData.ephemerisResponse = it
                gnssComputationData.startedComputingDate = Date()
                gnssComputationData.computationSettings = computationSettings

                gnssData.ephemerisResponse = it
                gnssData.lastEphemerisDate = Date()
                gnssData.modes = ModeParser.parseSettingsListToModeList(computationSettings)

                emitter.onSuccess("")
            }, {
                emitter.onError(Throwable())
            }, CompositeDisposable())
        }
    }

    override fun stopComputing() {
        gnssComputationData = GnssComputationData()
        gnssData = GnssData()
    }

    override fun setStatus(gnssStatus: GnssStatus) {
        gnssComputationData.gnssStatus = gnssStatus
        gnssData.lastGnssStatus = gnssStatus
    }

    override fun setMeasurement(measurementsEvent: GnssMeasurementsEvent) {
        val clock = measurementsEvent.clock
        measurementsEvent.measurements.let {
            if (it.isNotEmpty() && gnssData.lastGnssStatus != null) {
                val measurementData = MeasurementData(
                    gnssData.lastGnssStatus,
                    it,
                    clock
                )
                gnssData.measurements.add(measurementData)
            }
        }
        gnssComputationData.ephemerisResponse?.let { ephemeris ->
            gnssComputationData.gnssStatus?.let { status ->
                val epoch =
                    EpochAcquisitionDataBuilder.mapToEpoch(measurementsEvent, status, ephemeris)
                computePvt(epoch)
            }
        }
    }

    private fun computePvt(epoch: Epoch) {
        gnssComputationData.epochs.add(epoch)

        if (isMeanTimePassed()) {
            val pvtInputData = PvtInputDataMapper.mapToPvtInputData(gnssComputationData)
            val name = "${Date()}.txt"
            Logger.savePvtInputData(name, pvtInputData)
            Logger.saveGnssDataForInariComparison(name, gnssData)
            pvtComputationInteractor.computePosition(pvtInputData)
        }
    }

    private fun isMeanTimePassed() =
        Date().time - gnssComputationData.startedComputingDate.time > 5000L
}

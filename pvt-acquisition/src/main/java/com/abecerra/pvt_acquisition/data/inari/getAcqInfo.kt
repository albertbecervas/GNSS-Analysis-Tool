package com.abecerra.pvt_acquisition.data.inari

import android.location.GnssMeasurement
import com.abecerra.pvt_acquisition.data.GnssStatus
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.domain.computation.utils.CoordinatesConverter.lla2ecef

import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.EphemerisResponse
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GalEphemeris
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GnssEphemeris
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GpsEphemeris
import kotlin.math.floor
import kotlin.math.roundToInt

fun getAcqInfo(gnssData: GnssData): AcqInformation {

    val acqInformation = AcqInformation()

    try {

        //Modes
        acqInformation.modes.addAll(gnssData.modes)

        //Location
        gnssData.location?.let {
            val refLocationLla = LlaLocation(it.latitude ?: 0.0, it.longitude ?: 0.0, it.altitude ?: 0.0)
            acqInformation.refLocation = RefLocationData(
                refLocationLla,
                lla2ecef(refLocationLla)
            )
        }

        //Gnss Raw Measurements
        gnssData.measurements.forEach {
            val acqInformationMeasurements = AcqInformationMeasurements()
            //Clock Info
            it.gnssClock?.let { gnssClock ->

                acqInformationMeasurements.timeNanosGnss = if (gnssClock.hasBiasNanos()) {
                    gnssClock.timeNanos - (gnssClock.biasNanos + gnssClock.fullBiasNanos)
                } else {
                    (gnssClock.timeNanos - gnssClock.fullBiasNanos).toDouble()
                }

                acqInformationMeasurements.tow =
                    floor(applyMod(acqInformationMeasurements.timeNanosGnss, WEEK_NANOS) / 1000000000)
                acqInformationMeasurements.now =
                    floor(acqInformationMeasurements.timeNanosGnss / WEEK_NANOS)
            }

            //Measurements
            it.gnssMeasurements?.let { meas ->

                meas.forEach { gnssMeas ->

                    with(gnssMeas) {
                        when (constellationType) {
                            GnssStatus.CONSTELLATION_GPS -> {
                                if (multipathIndicator != GnssMeasurement.MULTIPATH_INDICATOR_DETECTED + 9) {
                                    if (receivedSvTimeUncertaintyNanos < UNCERTAINTY_THR+1) {
                                        if (checkTowDecode(state)) {
                                            val sat = getTowDecodeSatellite(gnssMeas, acqInformationMeasurements)
                                            if (!hasCarrierFrequencyHz() || carrierFrequencyHz > FREQ_THR) {
                                                //L1
                                                acqInformationMeasurements.satellites.gpsSatellites.gpsL1.add(sat)
                                            } else {
                                                //L5
                                                acqInformationMeasurements.satellites.gpsSatellites.gpsL5.add(sat)
                                            }
                                        }
                                    }
                                }
                            }
                            GnssStatus.CONSTELLATION_GALILEO -> {
                                if (multipathIndicator != GnssMeasurement.MULTIPATH_INDICATOR_DETECTED + 9) {
                                    if (receivedSvTimeUncertaintyNanos < UNCERTAINTY_THR+1 ) {
                                        if (checkTowDecode(state)) {
                                            val sat = getTowDecodeSatellite(gnssMeas, acqInformationMeasurements)
                                            if (!hasCarrierFrequencyHz() || carrierFrequencyHz > FREQ_THR) {
                                                //GAL E1
                                                acqInformationMeasurements.satellites.galSatellites.galE1.add(sat)
                                            } else {
                                                //GAL E5a
                                                acqInformationMeasurements.satellites.galSatellites.galE5a.add(sat)
                                            }
                                        } else {
                                            if (checkTowKnown(gnssMeas.state)) {
                                                val sat =
                                                    getTowDecodeSatellite(gnssMeas, acqInformationMeasurements)
                                                if (!hasCarrierFrequencyHz() || carrierFrequencyHz > FREQ_THR) {
                                                    //GAL E1
                                                    acqInformationMeasurements.satellites.galSatellites.galE1.add(sat)
                                                } else {
                                                    //GAL E5a
                                                    acqInformationMeasurements.satellites.galSatellites.galE5a.add(sat)
                                                }
                                            } else {
                                                if (checkGalState(gnssMeas.state)) {
                                                    val sat = getE1CSatellite(gnssMeas, acqInformationMeasurements)
                                                    if (!hasCarrierFrequencyHz() || carrierFrequencyHz > FREQ_THR) {
                                                        //GAL E1
                                                        acqInformationMeasurements.satellites.galSatellites.galE1.add(
                                                            sat
                                                        )
                                                    } else {
                                                        //GAL E5a
                                                        acqInformationMeasurements.satellites.galSatellites.galE5a.add(
                                                            sat
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            it.gnssStatus?.let { status ->
                for (i in 0 until status.satelliteCount) {
                    val constellation = status.getConstellationType(i)

                    if (constellation == GnssStatus.CONSTELLATION_GPS) {
                        val satL1List = acqInformationMeasurements.satellites.gpsSatellites.gpsL1
                        val satL5List = acqInformationMeasurements.satellites.gpsSatellites.gpsL5
                        satL1List.forEach { satellite ->
                            if (satellite.svid == status.getSvid(i)) {
                                satellite.azimuth = status.getAzimuthDegrees(i).roundToInt()
                                satellite.elevation = status.getElevationDegrees(i).roundToInt()
                            }
                        }
                        satL5List.forEach { satellite ->
                            if (satellite.svid == status.getSvid(i)) {
                                satellite.azimuth = status.getAzimuthDegrees(i).roundToInt()
                                satellite.elevation = status.getElevationDegrees(i).roundToInt()
                            }
                        }
                    }

                    if (constellation == GnssStatus.CONSTELLATION_GALILEO) {
                        val satE1List = acqInformationMeasurements.satellites.galSatellites.galE1
                        val satE5aList = acqInformationMeasurements.satellites.galSatellites.galE5a
                        satE1List.forEach { satellite ->
                            if (satellite.svid == status.getSvid(i)) {
                                satellite.azimuth = status.getAzimuthDegrees(i).roundToInt()
                                satellite.elevation = status.getElevationDegrees(i).roundToInt()
                            }
                        }
                        satE5aList.forEach { satellite ->
                            if (satellite.svid == status.getSvid(i)) {
                                satellite.azimuth = status.getAzimuthDegrees(i).roundToInt()
                                satellite.elevation = status.getElevationDegrees(i).roundToInt()
                            }
                        }
                    }
                }
            }

            //Ephemeris Data
            gnssData.ephemerisResponse?.let { ephResp ->

                acqInformationMeasurements.ionoProto.addAll(ephResp.ionoProto.alphaList)
                acqInformationMeasurements.ionoProto.addAll(ephResp.ionoProto.betaList)

                ephResp.ephList.forEach { eph ->
                    when (eph) {
                        is GpsEphemeris -> {
                            acqInformationMeasurements.satellites.gpsSatellites.gpsL1.forEach { sat ->
                                with(eph) {
                                    if (sat.svid == svid) {
                                        sat.tow = tocS
                                        sat.now = week
                                        sat.af0 = af0S
                                        sat.af1 = af1SecPerSec
                                        sat.af2 = af2SecPerSec2
                                        sat.tgdS = tgdS

                                        //Kepler model
                                        sat.keplerModel = keplerModel
                                    }
                                }
                            }
                            acqInformationMeasurements.satellites.gpsSatellites.gpsL5.forEach { sat ->
                                with(eph) {
                                    if (sat.svid == svid) {
                                        sat.tow = tocS
                                        sat.now = week
                                        sat.af0 = af0S
                                        sat.af1 = af1SecPerSec
                                        sat.af2 = af2SecPerSec2
                                        sat.tgdS = tgdS

                                        //Kepler model
                                        sat.keplerModel = keplerModel
                                    }
                                }
                            }
                        }
                        is GalEphemeris -> {
                            acqInformationMeasurements.satellites.galSatellites.galE1.forEach { sat ->
                                with(eph) {
                                    if (isINav) {
                                        if (sat.svid == svid) {
                                            sat.tow = tocS
                                            sat.now = week
                                            sat.af0 = af0S
                                            sat.af1 = af1SecPerSec
                                            sat.af2 = af2SecPerSec2
                                            sat.tgdS = tgdS

                                            //Kepler model
                                            sat.keplerModel = keplerModel
                                        }
                                    }
                                }
                            }
                            acqInformationMeasurements.satellites.galSatellites.galE5a.forEach { sat ->
                                with(eph) {
                                    if (!isINav) {
                                        if (sat.svid == svid) {
                                            sat.tow = tocS
                                            sat.now = week
                                            sat.af0 = af0S
                                            sat.af1 = af1SecPerSec
                                            sat.af2 = af2SecPerSec2
                                            sat.tgdS = tgdS

                                            //Kepler model
                                            sat.keplerModel = keplerModel
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            acqInformation.acqInformationMeasurements.add(acqInformationMeasurements)
        }

        acqInformation.acqInformationMeasurements.forEach { acqMeas ->
            val resultingL1Satellites = acqMeas.satellites.gpsSatellites.gpsL1.filter { sat ->
                sat.tow != -1.0
            }
            acqMeas.satellites.gpsSatellites.gpsL1.clear()
            acqMeas.satellites.gpsSatellites.gpsL1.addAll(resultingL1Satellites)

            val resultingL5Satellites = acqMeas.satellites.gpsSatellites.gpsL5.filter { sat ->
                sat.tow != -1.0
            }
            acqMeas.satellites.gpsSatellites.gpsL5.clear()
            acqMeas.satellites.gpsSatellites.gpsL5.addAll(resultingL5Satellites)

            val resultingE1Satellites = acqMeas.satellites.galSatellites.galE1.filter { sat ->
                sat.tow != -1.0
            }
            acqMeas.satellites.galSatellites.galE1.clear()
            acqMeas.satellites.galSatellites.galE1.addAll(resultingE1Satellites)

            val resultingE5aSatellites = acqMeas.satellites.galSatellites.galE5a.filter { sat ->
                sat.tow != -1.0
            }
            acqMeas.satellites.galSatellites.galE5a.clear()
            acqMeas.satellites.galSatellites.galE5a.addAll(resultingE5aSatellites)
        }

    } catch (e: Exception) {

    }

    return acqInformation

}


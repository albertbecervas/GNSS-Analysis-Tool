package com.abecerra.pvt_acquisition.domain.acquisition

import com.abecerra.pvt_acquisition.data.mapper.KeplerianModelMapper
import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.PvtConstants.CONST_GAL
import com.abecerra.pvt_computation.data.PvtConstants.CONST_GPS
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.input.SatelliteEphemeris
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.EphemerisResponse
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GalEphemeris
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GnssEphemeris
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.GpsEphemeris

fun Epoch.mapEphemerisResponse(ephemerisResponse: EphemerisResponse){
    ionoProto.addAll(ephemerisResponse.ionoProto.alphaList)
    ionoProto.addAll(ephemerisResponse.ionoProto.betaList)
    with(ephemerisResponse) {
        this.ephList.forEach { eph -> findSatelliteAndSetEphemeris(eph) }
    }
}

private fun Epoch.findSatelliteAndSetEphemeris(eph: GnssEphemeris) {
    parseEphemeris(eph)?.let { satEphemeris ->
        satellitesMeasurements.forEach {
            if (it.svid == eph.svid && it.constellation == satEphemeris.satConst){
                it.satelliteEphemeris = satEphemeris
            }
        }
    }
}

fun parseEphemeris(gnssEphemeris: GnssEphemeris): SatelliteEphemeris? {
    return with(gnssEphemeris) {
        when (this) {
            is GpsEphemeris -> {
                SatelliteEphemeris(CONST_GPS, tocS, week, af0S, af1SecPerSec,
                    af2SecPerSec2, tgdS, KeplerianModelMapper.mapKeplerianModel(keplerModel))
            }
            is GalEphemeris -> {
                SatelliteEphemeris(CONST_GAL, tocS, week, af0S, af1SecPerSec,
                    af2SecPerSec2, tgdS, KeplerianModelMapper.mapKeplerianModel(keplerModel))
            }
            else -> {
                null
            }
        }
    }
}

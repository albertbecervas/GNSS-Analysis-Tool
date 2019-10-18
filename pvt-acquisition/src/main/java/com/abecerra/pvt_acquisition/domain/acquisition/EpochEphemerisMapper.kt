package com.abecerra.pvt_acquisition.domain.acquisition

import com.abecerra.pvt_acquisition.data.mapper.KeplerianModelMapper
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.data.input.SatelliteEphemeris
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.*

fun Epoch.mapEphemerisResponse(ephemerisResponse: EphemerisResponse){
    with(ephemerisResponse) {
        this.ephList.forEach { eph -> findSatelliteAndSetEphemeris(eph) }
    }
}

private fun Epoch.findSatelliteAndSetEphemeris(eph: GnssEphemeris) {
    satellitesMeasurements.find { it.svid == eph.svid }?.let {
            parseEphemeris(eph)?.let { satEph -> it.satelliteEphemeris = satEph }
        }
}

fun parseEphemeris(gnssEphemeris: GnssEphemeris): SatelliteEphemeris? {
    return with(gnssEphemeris) {
        when (this) {
            is GpsEphemeris -> {
                SatelliteEphemeris(tocS, week, af0S, af1SecPerSec,
                    af2SecPerSec2, tgdS, KeplerianModelMapper.mapKeplerianModel(keplerModel))
            }
            is GalEphemeris -> {
                SatelliteEphemeris(tocS, week, af0S, af1SecPerSec,
                    af2SecPerSec2, tgdS, KeplerianModelMapper.mapKeplerianModel(keplerModel))
            }
            else -> {
                null
            }
        }
    }
}

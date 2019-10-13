package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.suplclient.ephemeris.GalEphemeris
import com.abecerra.pvt_computation.suplclient.ephemeris.GnssEphemeris
import com.abecerra.pvt_computation.suplclient.ephemeris.GpsEphemeris
import com.abecerra.pvt_computation.suplclient.ephemeris.KeplerianModel

data class SatelliteEphemeris(
    var tow: Double = -1.0,
    var now: Int = 0,
    var af0: Double = 0.0,
    var af1: Double = 0.0,
    var af2: Double = 0.0,
    var tgdS: Double = 0.0,
    var keplerModel: KeplerianModel? = null
) {

    fun parseEphemeris(gnssEphemeris: GnssEphemeris) {
        with(gnssEphemeris) {
            when (this) {
                is GpsEphemeris -> {
                    setEphemeris(tocS, week, af0S, af1SecPerSec, af2SecPerSec2, tgdS, keplerModel)
                }
                is GalEphemeris -> {
                    setEphemeris(tocS, week, af0S, af1SecPerSec, af2SecPerSec2, tgdS, keplerModel)
                }
            }
        }
    }

    private fun setEphemeris(
        tow: Double,
        now: Int,
        af0: Double,
        af1: Double,
        af2: Double,
        tgdS: Double,
        keplerModel: KeplerianModel?
    ) {
        this.tow = tow
        this.now = now
        this.af0 = af0
        this.af1 = af1
        this.af2 = af2
        this.tgdS = tgdS

        //Kepler model
        this.keplerModel = keplerModel
    }
}

package com.abecerra.pvt.computation.data

import com.abecerra.pvt.suplclient.ephemeris.GalEphemeris
import com.abecerra.pvt.suplclient.ephemeris.GnssEphemeris
import com.abecerra.pvt.suplclient.ephemeris.GpsEphemeris
import com.abecerra.pvt.suplclient.ephemeris.KeplerianModel

const val UNCERTAINTY_THR = 1000000000L
const val WEEK_NANOS = 604800000000000
const val GAL_E1C = 100000000L
const val FREQ_THR = 1400000000

data class GnssData(
    var cn0mask: Int = 0,
    var elevationMask: Int = 0,
    var refLocation: Location = Location(),
    var computationSettings: List<ComputationSettings> = arrayListOf(),
    var epochMeasurements: ArrayList<Epoch> = arrayListOf()
)

data class ComputationSettings(
    var id: Int,
    val name: String = "",
    val constellations: ArrayList<Int>,
    val bands: ArrayList<Int>,
    val corrections: ArrayList<Int>,
    val algorithm: Int,
    var isSelected: Boolean,
    var color: Int = -1
)

data class Epoch(
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0,
    var now: Double = 0.0,
    var ionoProto: ArrayList<Double> = arrayListOf(),
    var satellitesMeasurements: ArrayList<SatelliteMeasurements> = arrayListOf()
)

data class SatelliteMeasurements(
    var svid: Int = 0,
    var constellation: Int = -1,
    var state: Int = 0,
    val multiPath: Int = 0,
    val carrierFreq: Double = 0.0,
    val tTx: Double = 0.0,
    val tRx: Double = 0.0,
    val cn0: Double = 0.0,
    val pR: Double = 0.0,
    var azimuth: Int = 0,
    var elevation: Int = 0,
    var satelliteEphemeris: SatelliteEphemeris = SatelliteEphemeris()
)

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
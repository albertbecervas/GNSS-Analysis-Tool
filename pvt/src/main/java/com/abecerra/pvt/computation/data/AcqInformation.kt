package com.abecerra.pvt.computation.data

import com.abecerra.pvt.suplclient.ephemeris.KeplerianModel


data class Satellites(
    var gpsSatellites: GPSSatellites = GPSSatellites(),
    var galSatellites: GALSatellites = GALSatellites()
)

data class GPSSatellites(
    var gpsL1: ArrayList<Satellite> = arrayListOf(),
    var gpsL5: ArrayList<Satellite> = arrayListOf()
)

data class GALSatellites(
    var galE1: ArrayList<Satellite> = arrayListOf(),
    var galE5a: ArrayList<Satellite> = arrayListOf()
)

data class Satellite(
    var svid: Int = 0,
    var state: Int = 0,
    val multiPath: Int = 0,
    val carrierFreq: Double = 0.0,
    val tTx: Double = 0.0,
    val tRx: Double = 0.0,
    val cn0: Double = 0.0,
    val pR: Double = 0.0,
    var azimuth: Int = 0,
    var elevation: Int = 0,
    var tow: Double = -1.0,
    var now: Int = 0,
    var af0: Double = 0.0,
    var af1: Double = 0.0,
    var af2: Double = 0.0,
    var tgdS: Double = 0.0,
    var keplerModel: KeplerianModel? = null
)







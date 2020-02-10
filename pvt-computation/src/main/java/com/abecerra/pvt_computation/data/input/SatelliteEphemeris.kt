package com.abecerra.pvt_computation.data.input

import com.abecerra.pvt_computation.data.ephemeris.KeplerianModel

data class SatelliteEphemeris(
    var satConst: Int = -1,
    var tow: Double = -1.0,
    var now: Int = 0,
    var af0: Double = 0.0,
    var af1: Double = 0.0,
    var af2: Double = 0.0,
    var tgdS: Double = 0.0,
    var keplerModel: KeplerianModel? = null
)

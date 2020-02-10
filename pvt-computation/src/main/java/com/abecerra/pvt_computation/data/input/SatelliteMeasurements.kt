package com.abecerra.pvt_computation.data.input

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

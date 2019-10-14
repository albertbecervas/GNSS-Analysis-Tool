package com.abecerra.pvt_computation.data

data class PvtFix(
    var location: Location,
    var time: Double
)

data class Location(
    var llaLocation: LlaLocation = LlaLocation(),
    var ecefLocation: EcefLocation = EcefLocation()
)

data class LlaLocation(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0
)

data class EcefLocation(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0
)


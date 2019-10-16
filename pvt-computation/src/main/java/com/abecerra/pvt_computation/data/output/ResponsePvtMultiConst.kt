package com.abecerra.pvt_computation.data.output

data class PvtLatLng(
    var lat: Double = 360.0,
    var lng: Double = 360.0,
    var altitude: Double = 360.0,
    var clockBias: Double = -10.0
)

data class PvtEcef(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var z: Double = -1.0,
    var clockBias: Double = 0.0,
    var interSystemBias: Double = 0.0

)

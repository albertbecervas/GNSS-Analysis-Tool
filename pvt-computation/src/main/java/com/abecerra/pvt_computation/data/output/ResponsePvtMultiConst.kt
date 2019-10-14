package com.abecerra.pvt_computation.data.output

data class ResponsePvtMultiConst(
    var pvt: PvtLatLng = PvtLatLng(),
    var dop: Dop = Dop(),
    var residue: Double = 0.0,
    var corrections: Corrections = Corrections()
)

data class PvtLatLng(
    var lat: Double = 360.0,
    var lng: Double = 360.0,
    var altitude: Double = 360.0,
    var time: Double = -10.0
)

data class PvtEcef(
    var x: Double = -1.0,
    var y: Double = -1.0,
    var z: Double = -1.0,
    var time: Double = 0.0

)

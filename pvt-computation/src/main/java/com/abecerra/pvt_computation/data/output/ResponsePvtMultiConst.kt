package com.abecerra.pvt_computation.data.output

data class ResponsePvtMultiConst(
    var pvt: PvtLatLng = PvtLatLng(),
    var dop: Dop = Dop(),
    var residue: Double = 0.0,
    var corrections: Corrections = Corrections(),
    var nSats: Float = 0f
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

data class Dop(
    var gDop: Double = -1.0,
    var pDop: Double = -1.0,
    var tDop: Double = -1.0
)

data class Corrections(
    var gpsIono: Double = 0.0,
    var gpsTropo: Double = 0.0,
    var galIono: Double = 0.0,
    var galTropo: Double = 0.0,
    var freq2: Double = 0.0
)

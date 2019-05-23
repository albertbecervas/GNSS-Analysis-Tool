package com.abecerra.pvt.computation.data

const val UNCERTAINTY_THR = 1000000000L
const val WEEK_NANOS = 604800000000000
const val GAL_E1C = 100000000L
const val FREQ_THR = 1400000000

data class GNSSData(
    var cn0mask: Int = 0,
    var elevationMask: Int = 0,
    var computationSettings: ArrayList<ComputationSettings> = arrayListOf(),
    var refLocation: LlaLocation = LlaLocation(),
    var gnssMeasurements: ArrayList<GNSSMeasurement> = arrayListOf()
)

data class GNSSMeasurement(
    val svList: ArrayList<SvInfo> = arrayListOf(),
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0,
    var now: Double = 0.0,
    val totalSatNumber: Int = 0,
    var ionoProto: ArrayList<Double> = arrayListOf(),
    val satellites: Satellites = Satellites()
)

data class SvInfo(
    var svIds: ArrayList<Int> = arrayListOf()
)

data class RefLocationData(
    val refLocationLla: LlaLocation = LlaLocation(),
    var refLocationEcef: EcefLocation = EcefLocation()
)
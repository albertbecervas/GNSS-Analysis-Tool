package com.abecerra.pvt_acquisition.data.inari

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.KeplerianModel

const val UNCERTAINTY_THR = 1000000000L
const val WEEK_NANOS = 604800000000000
const val GAL_E1C = 100000000L
const val FREQ_THR = 1400000000

data class AcqInformation(
    var cn0mask: Int = 0,
    var elevationMask: Int = 0,
    var modes: ArrayList<Mode> = arrayListOf(),
    var refLocation: RefLocationData = RefLocationData(),
    var acqInformationMeasurements: ArrayList<AcqInformationMeasurements> = arrayListOf()
)

data class AcqInformationMeasurements(
    val svList: ArrayList<SvInfo> = arrayListOf(),
    var timeNanosGnss: Double = 0.0,
    var tow: Double = 0.0, //testear
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
    val refLocationEcef: EcefLocation = EcefLocation()
)

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







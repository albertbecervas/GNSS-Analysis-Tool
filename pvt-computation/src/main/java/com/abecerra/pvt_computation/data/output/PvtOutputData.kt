package com.abecerra.pvt_computation.data.output

import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.input.ComputationSettings
import java.io.Serializable

data class PvtOutputData(
    var pvtFix: PvtFix,
    var refPosition: LlaLocation,
    var computationSettings: ComputationSettings,
    var corrections: Corrections = Corrections(),
    var dop: Dop = Dop(),
    var residue: Double = 0.0,
    var nSats: Float = 0f,
    var gpsTime: GpsTime = GpsTime(0)
) : Serializable

data class Corrections(
    var gpsIono: Double = 0.0,
    var gpsTropo: Double = 0.0,
    var galIono: Double = 0.0,
    var galTropo: Double = 0.0,
    var freq2: Double = 0.0,
    var gpsElevIono: ArrayList<Pair<Int, Double>> = arrayListOf(),
    var galElevIono: ArrayList<Pair<Int, Double>> = arrayListOf()
)

data class Dop(
    var gDop: Double = -1.0,
    var pDop: Double = -1.0,
    var tDop: Double = -1.0
)



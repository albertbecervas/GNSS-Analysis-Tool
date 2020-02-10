package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.output.Corrections
import com.abecerra.pvt_computation.data.output.Dop
import com.abecerra.pvt_computation.data.output.GpsTime
import com.abecerra.pvt_computation.data.output.PvtFix

data class PvtAlgorithmOutputData(
    val pvtFix: PvtFix = PvtFix(),
    val dop: Dop = Dop(),
    val residue: Double = 0.0,
    val corrections: Corrections = Corrections(),
    val nSatellites: Float = 0f,
    val gpsTime: GpsTime = GpsTime(0)
)

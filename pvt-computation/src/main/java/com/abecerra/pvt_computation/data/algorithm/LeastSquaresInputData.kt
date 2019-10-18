package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements

data class LeastSquaresInputData(
    val a: ArrayList<ArrayList<Double>> = arrayListOf(),
    var p: ArrayList<Double> = arrayListOf(),
    val tCorr: ArrayList<Double> = arrayListOf(),
    var pCorr: ArrayList<Double> = arrayListOf(),
    val x: ArrayList<EcefLocation> = arrayListOf(),
    val pR: ArrayList<Double> = arrayListOf<Double>(),
    val svn: ArrayList<Int> = arrayListOf<Int>(),
    val cn0: ArrayList<Double> = arrayListOf<Double>(),
    val satellites: ArrayList<SatelliteMeasurements> = arrayListOf<SatelliteMeasurements>(),
    var corr: Double = 0.0,
    var pRc: Double = 0.0,
    var d0: Double = 0.0,
    var aX: Double = 0.0,
    var aY: Double = 0.0,
    var aZ: Double = 0.0,
    var refPosition: EcefLocation = EcefLocation(),
    var isMultiC: Boolean = false,
    var isWeight: Boolean = false
)

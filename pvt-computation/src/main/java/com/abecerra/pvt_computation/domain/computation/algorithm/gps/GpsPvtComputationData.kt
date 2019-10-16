package com.abecerra.pvt_computation.domain.computation.algorithm.gps

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmComputationInputData

data class GpsPvtComputationData(
    val gpsA: ArrayList<ArrayList<Double>> = arrayListOf(),
    val gpsP: ArrayList<Double> = arrayListOf(),
    val gpsTcorr: ArrayList<Double> = arrayListOf(),
    var gpsPcorr: ArrayList<Double> = arrayListOf(),
    val gpsX: ArrayList<EcefLocation> = arrayListOf(),
    val gpsPr: ArrayList<Double> = arrayListOf<Double>(),
    val gpsSvn: ArrayList<Int> = arrayListOf<Int>(),
    val gpsCn0: ArrayList<Double> = arrayListOf<Double>(),
    val gpsSatellites: ArrayList<SatelliteMeasurements> = arrayListOf<SatelliteMeasurements>(),
    var gpsCorr: Double = 0.0,
    var gpsPrC: Double = 0.0,
    var gpsD0: Double = 0.0,
    var gpsAx: Double = 0.0,
    var gpsAy: Double = 0.0,
    var gpsAz: Double = 0.0
) : PvtAlgorithmComputationInputData() {

    override fun getA(): ArrayList<ArrayList<Double>> = gpsA

    override fun getP(): ArrayList<Double> = gpsP

    override fun getTCorr(): ArrayList<Double> = gpsTcorr

    override fun getPCorr(): ArrayList<Double> = gpsPcorr

    override fun getX(): ArrayList<EcefLocation> = gpsX

    override fun getPr(): ArrayList<Double> = gpsPr

    override fun getSvn(): ArrayList<Int> = gpsSvn

    override fun getCn0(): ArrayList<Double> = gpsCn0

    override fun getSatellites(): ArrayList<SatelliteMeasurements> = gpsSatellites

    override fun getCorr(): Double = gpsCorr

    override fun getPrC(): Double = gpsPrC

    override fun getD0(): Double = gpsD0

    override fun getAx(): Double = gpsAx

    override fun getAy(): Double = gpsAy

    override fun getAz(): Double = gpsAz
}

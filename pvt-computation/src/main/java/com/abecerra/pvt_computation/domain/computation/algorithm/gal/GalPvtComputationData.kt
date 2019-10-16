package com.abecerra.pvt_computation.domain.computation.algorithm.gal

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmComputationInputData

data class GalPvtComputationData(
    val galA: ArrayList<ArrayList<Double>> = arrayListOf(),
    val galP: ArrayList<Double> = arrayListOf(),
    val galTcorr: ArrayList<Double> = arrayListOf(),
    var galPcorr: ArrayList<Double> = arrayListOf(),
    val galX: ArrayList<EcefLocation> = arrayListOf(),
    val galPr: ArrayList<Double> = arrayListOf<Double>(),
    val galSvn: ArrayList<Int> = arrayListOf<Int>(),
    val galCn0: ArrayList<Double> = arrayListOf<Double>(),
    val galSatellites: ArrayList<SatelliteMeasurements> = arrayListOf<SatelliteMeasurements>(),
    var galCorr: Double = 0.0,
    var galPrC: Double = 0.0,
    var galD0: Double = 0.0,
    var galAx: Double = 0.0,
    var galAy: Double = 0.0,
    var galAz: Double = 0.0
) : PvtAlgorithmComputationInputData() {

    override fun getA(): ArrayList<ArrayList<Double>> = galA

    override fun getP(): ArrayList<Double> = galP

    override fun getTCorr(): ArrayList<Double> = galTcorr

    override fun getPCorr(): ArrayList<Double> = galPcorr

    override fun getX(): ArrayList<EcefLocation> = galX

    override fun getPr(): ArrayList<Double> = galPr

    override fun getSvn(): ArrayList<Int> = galSvn

    override fun getCn0(): ArrayList<Double> = galCn0

    override fun getSatellites(): ArrayList<SatelliteMeasurements> = galSatellites

    override fun getCorr(): Double = galCorr

    override fun getPrC(): Double = galPrC

    override fun getD0(): Double = galD0

    override fun getAx(): Double = galAx

    override fun getAy(): Double = galAy

    override fun getAz(): Double = galAz
}

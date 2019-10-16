package com.abecerra.pvt_computation.data.algorithm

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements

abstract class PvtAlgorithmComputationInputData {
    abstract fun getA(): ArrayList<ArrayList<Double>>
    abstract fun getP(): ArrayList<Double>
    abstract fun getTCorr(): ArrayList<Double>
    abstract fun getPCorr(): ArrayList<Double>
    abstract fun getX(): ArrayList<EcefLocation>
    abstract fun getPr(): ArrayList<Double>
    abstract fun getSvn(): ArrayList<Int>
    abstract fun getCn0(): ArrayList<Double>
    abstract fun getSatellites(): ArrayList<SatelliteMeasurements>
    abstract fun getCorr(): Double
    abstract fun getPrC(): Double
    abstract fun getD0(): Double
    abstract fun getAx(): Double
    abstract fun getAy(): Double
    abstract fun getAz(): Double
}

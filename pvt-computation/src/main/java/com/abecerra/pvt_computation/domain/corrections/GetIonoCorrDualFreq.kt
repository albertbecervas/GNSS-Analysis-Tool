package com.abecerra.pvt_computation.domain.corrections

import kotlin.math.pow

/**
 * Computes ionosphere correction using band 'frequencies[1]' and band 'frequencies[0]' measurements
 */
fun getIonoCorrDualFreq(frequencies: ArrayList<Double>, pseudoranges: ArrayList<Double>): Double {
    var corr = 0.0

    val freq1 = frequencies[0]
    val freq2 = frequencies[1]
    val pr1 = pseudoranges[0]
    val pr2 = pseudoranges[1]

    val prIF = ((freq1.pow(2.0) * pr1) - (freq2.pow(2.0) * pr2)) / (freq1.pow(2.0) - freq2.pow(2.0))


    return corr
}

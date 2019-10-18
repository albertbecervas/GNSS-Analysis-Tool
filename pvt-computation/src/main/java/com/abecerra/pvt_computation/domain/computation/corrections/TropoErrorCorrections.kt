package com.abecerra.pvt_computation.domain.computation.corrections


import com.abecerra.pvt_computation.data.Constants.PRESSURE
import com.abecerra.pvt_computation.data.Constants.TEMPERATURE
import kotlin.math.*

/**
 * Computation of the pseudorange correction due to tropospheric refraction.
 * Saastamoinen algorithm.
 */
fun tropoErrorCorrection(elevation: ArrayList<Double>, altitude: ArrayList<Double>): Double {

    var corr = 0.0

    //Saastamoinen model requires positive ellipsoidal height
    val height = altitude.map {
        if (it < 0.0) 0.0 else it
    }

    if (height.min() ?: 5000.0 < 5000) {

        //Elevation in rad
        val elev = elevation.map {
            abs(it) * PI / 180
        }
        //numerical constants for the algorithm [-] [m] [mbar]
        val hR = 50.0

        val p = arrayListOf<Double>()
        val t = arrayListOf<Double>()
        val h = arrayListOf<Double>()
        height.forEach {
            p.add(PRESSURE * (1 - 0.0000226 * it).pow(5.225))
            t.add(TEMPERATURE - 0.0065 * it)
            h.add(hR * exp(-0.0006396 * it))
        }

        //Linear interpolation
        val hA = arrayListOf(0, 500, 1000, 1500, 2000, 2500, 3000, 4000, 5000)
        val bA = arrayListOf(1.156, 1.079, 1.006, 0.938, 0.874, 0.813, 0.757, 0.654, 0.563)

        val tValues = arrayListOf<Double>()
        val bValues = arrayListOf<Double>()

        for (i in 0 until t.size) {
            val d = hA.map { it - height[i] }
            val dMinIndex = d.indices.minBy { abs(d[it]) } ?: 0
            val index = if (d[dMinIndex] > 0) {
                arrayListOf(dMinIndex - 1, dMinIndex)
            } else {
                arrayListOf(dMinIndex, dMinIndex + 1)
            }

            tValues.add(i, (height[i] - hA[index[0]]) / (hA[index[1]] - hA[index[0]]))
            bValues.add(i, (1 - tValues[i]) * bA[index[0]] + tValues[i] * bA[index[1]])
        }

        val e = arrayListOf<Double>()
        val corrList = arrayListOf<Double>()
        repeat(height.size) { i ->
            e.add(0.01 * h[i] * exp(-37.2465 + 0.213166 * t[i] - 0.000256908 * t[i].pow(2.0)))
            corrList.add(
                (0.002277 / sin(elev[i])) * (p[i] - (bValues[i] / (tan(elev[i])).pow(2.0)))
                        + (0.002277 / sin(elev[i])) * (1255 / t[i] + 0.05) * e[i]
            )
        }

        if (corrList.isNotEmpty()) {
            corr = corrList[0]
        }

    }

    return corr
}

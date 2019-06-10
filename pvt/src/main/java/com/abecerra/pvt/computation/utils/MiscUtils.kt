package com.abecerra.pvt.computation.utils

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM
import java.lang.Math.*

/**
 * Repairs over- and underflow of GPS time
 */
fun checkTime(time: Double): Double {
    val halfWeek = 302400.0
    var newTime = time

    if (time > halfWeek) newTime = time - 2 * halfWeek
    if (time < -halfWeek) newTime = time + 2 * halfWeek

    return newTime
}

/**
 * Returns rotated satellite ECEF coordinates due to Earth rotation during signal travel time
 */
fun earthRotCorr(travelTime: Double, xSat: DoubleArray): DoubleArray {
    val omegaeDot = 7.2921159e-5
    val omegaTau = omegaeDot * travelTime

    val matrixArray = doubleArrayOf(
        cos(omegaTau), sin(omegaTau), 0.0,    // 1st row
        -sin(omegaTau), cos(omegaTau), 0.0,   // 2nd row
        0.0, 0.0, 1.0                         // 3rd row
    )

    val rMat = DMatrixRMaj.wrap(3, 3, matrixArray)
    val xSatVec = DMatrixRMaj.wrap(1, 3, xSat)
    val xSatRotVec = DMatrixRMaj.wrap(1, 3, doubleArrayOf(0.0, 0.0, 0.0))

    // xSatRotVec = rMat * xSatVec
    MatrixVectorMult_DDRM.mult(rMat, xSatVec, xSatRotVec)

    return xSatRotVec.getData() // Transfor vector to DoubleArray
}

/**
 * Transforms time in nanos to GPST
 */
fun nsgpst2gpst(timeNanos: Long): LongArray {
    val weekSeconds = 7L * 24L * 60L * 60L

    val timeSec = timeNanos / 1E9

    val now = floor(timeSec / weekSeconds).toLong()
    val tow = round(timeSec.rem(weekSeconds))

    return longArrayOf(tow, now)
}

/**
 * Compute Weight Matrix
 */
fun computeCNoWeightMatrix(cnos: List<Double>, isWeight: Boolean): DMatrixRMaj {
    var wMat = CommonOps_DDRM.identity(cnos.size, cnos.size)
    if (isWeight) {
        val diagonal = arrayListOf<Double>()
        val tmp = arrayListOf<Double>()
        cnos.forEach { cno ->
            tmp.add(pow(10.0, -cno / 10))
        }
        val sum = tmp.sum()
        tmp.forEach {
            diagonal.add(1 / (it / sum))
        }
        wMat = CommonOps_DDRM.diag(*diagonal.toDoubleArray())
    }

    return wMat
}
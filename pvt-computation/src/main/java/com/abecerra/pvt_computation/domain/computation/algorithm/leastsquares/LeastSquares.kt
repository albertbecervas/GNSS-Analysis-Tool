package com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData
import com.abecerra.pvt_computation.data.algorithm.PvtAlgorithmOutputData
import com.abecerra.pvt_computation.data.output.*
import com.abecerra.pvt_computation.domain.computation.utils.CoordinatesConverter.ecef2lla
import org.ejml.simple.SimpleMatrix
import kotlin.math.pow
import kotlin.math.sqrt


fun leastSquares(leastSquaresData: LeastSquaresInputData):
        PvtAlgorithmOutputData? {
    with(leastSquaresData) {
        var response: PvtAlgorithmOutputData? = null
        val position = PvtEcef(refPosition.x, refPosition.y, refPosition.z)
        try {
            val nSatellites = pR.size
            val nUnknowns = if (isMultiC) 5 else 4
            if (nSatellites >= nUnknowns) {
                if (a.size != nSatellites) {
                    println("A and p are not the same length")
                }

                // Weighted Least Squares: d = inv(G'*W*G)*G'*W*p

                // Column vector p of nSatellites rows
                val pVector =
                    SimpleMatrix(nSatellites, 1, true, pR.toDoubleArray())
                // Matrix G of nSatellites rows and nUnknowns columns
                val gMatrix = SimpleMatrix(nSatellites, nUnknowns)
                // Matrix W of nSatellites rows and nSatellites columns, identity if isWeight = false
                val wMatrix = computeCNoWeightMatrix(cn0, isWeight)

                a.forEachIndexed { row, a ->
                    gMatrix.set(row, 0, a[0])
                    gMatrix.set(row, 1, a[1])
                    gMatrix.set(row, 2, a[2])
                    gMatrix.set(row, 3, a[3])
                    if (nUnknowns == 5) gMatrix.set(row, 4, a[4])
                }

                // H = inv(G'*W*G)
                val hMatrix = gMatrix.transpose().mult(wMatrix).mult(gMatrix).invert()

                // d = inv(H)*G'*W*p
                val dHatVector = hMatrix.mult(gMatrix.transpose()).mult(wMatrix).mult(pVector)

                // W*G matrix
                val wgMatrix = wMatrix.mult(gMatrix)

                // Residue computation: res = p - WG*d
                val resVector = pVector.minus(wgMatrix.mult(dHatVector))

                // Residue =  |res| = |p - pHat|
                val residue = resVector.normF()

                // DOP computation
                val gDop = sqrt(hMatrix[0, 0] + hMatrix[1, 1] + hMatrix[2, 2] + hMatrix[3, 3])
                val pDop = sqrt(hMatrix[0, 0] + hMatrix[1, 1] + hMatrix[2, 2])
                val tDop = sqrt(hMatrix[3, 3])
                val dop = Dop(gDop, pDop, tDop)

                // Save results
                position.x += dHatVector[0, 0]
                position.y += dHatVector[1, 0]
                position.z += dHatVector[2, 0]
                position.clockBias = dHatVector[3, 0]
                if (nUnknowns == 5) position.interSystemBias = dHatVector[4, 0]

                val ecefLocation = EcefLocation(position.x, position.y, position.z)
                val llaLocation = ecef2lla(ecefLocation)
                val clockBias = position.clockBias
                val pvtLatLng = PvtLatLng(
                    llaLocation.latitude, llaLocation.longitude, llaLocation.altitude, clockBias
                )

                response = PvtAlgorithmOutputData(
                    pvtFix = PvtFix(pvtLatLng),
                    dop = dop,
                    residue = residue,
                    corrections = Corrections(),
                    nSatellites = nSatellites.toFloat()
                )

            } else {
                print("LS ERROR:::not enough satellites\n")
            }
        } catch (e: Exception) {
            print("LS ERROR::: ${e.localizedMessage}")
        }

        return response
    }
}


/**
 * Compute Weight Matrix
 * C/N0 weighting method - Sigma e [Wieser, Andreas, et al. "An extended weight model for GPS phase observations"]
 */
fun computeCNoWeightMatrix(cnos: List<Double>, isWeight: Boolean): SimpleMatrix {
    var wMat = SimpleMatrix.identity(cnos.size)
    if (isWeight) {
        val diagonal = arrayListOf<Double>()

        cnos.forEach { cno ->
            val w = 0.244 * 10.0.pow(-0.1 * cno)
            diagonal.add(1 / w)
        }

        wMat = SimpleMatrix.diag(*diagonal.toDoubleArray())
    }
    return wMat
}

package com.abecerra.pvt_computation.domain.computation.utils

import com.abecerra.pvt_computation.data.*
import com.abecerra.pvt_computation.data.output.Dop
import com.abecerra.pvt_computation.data.output.ResponsePvtMultiConst
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import kotlin.math.pow
import kotlin.math.sqrt

@Throws(Exception::class)
fun leastSquares(
    position: PvtFix,
    arrayPr: List<Double>,
    arrayA: List<ArrayList<Double>>,
    isMultiC: Boolean,
    cnos: List<Double>,
    isWeight: Boolean
): ResponsePvtMultiConst {
    val nSats = arrayPr.size
    val nCols = if (isMultiC) 5 else 4
    var response = ResponsePvtMultiConst()
    if (nSats >= nCols) {
        if (arrayA.size != nSats) {
//            Timber.d("A and p are not the same length")
        }

        // PVT computation
        val daPr = arrayPr.toDoubleArray()
        var daA = doubleArrayOf()
        repeat(nSats) { ind ->
            daA += arrayA[ind]
        }

        val prMat = DMatrixRMaj.wrap(nSats, 1, daPr)
        val gMat = DMatrixRMaj.wrap(nSats, nCols, daA)

        // Weight Matrix
        val wMat = computeCNoWeightMatrix(cnos, isWeight)

        // gwMat = gMat' * wMat
        var temp = DoubleArray(nCols * nSats)
        val gwMat = DMatrixRMaj.wrap(nSats, nCols, temp)
        CommonOps_DDRM.multTransA(gMat, wMat, gwMat)

        // gwgMat = (gMat' * wMat * gMat)
        val temp2 = DoubleArray(nCols * nCols)
        val gwgMat = DMatrixRMaj.wrap(nCols, nCols, temp2)
        CommonOps_DDRM.mult(gwMat, gMat, gwgMat)

        // hMat = inv(gwgMat)
        val hMat = DMatrixRMaj.wrap(nCols, nCols, temp2)
        CommonOps_DDRM.invert(gwgMat, hMat)

        // hgwMat = hMat*gMat'*wMat
        val hgwMat = DMatrixRMaj.wrap(nCols, nSats, temp)
        CommonOps_DDRM.mult(hMat, gwMat, hgwMat)

        // Compute d vector
        val dMat = DMatrixRMaj.wrap(nCols, 1, temp)
        CommonOps_DDRM.mult(hgwMat, prMat, dMat)


        val dArray = arrayListOf<Double>()
        repeat(nCols) { i ->
            dArray.add(dMat[i])
        }

        // Obtain position
        position.location.ecefLocation.x += dArray[0]
        position.location.ecefLocation.y += dArray[1]
        position.location.ecefLocation.z += dArray[2]
        position.time += dArray[3] / Constants.C

        val ecefLocation = EcefLocation(
            position.location.ecefLocation.x,
            position.location.ecefLocation.y,
            position.location.ecefLocation.z
        )
        val llaLocation = CoordinatesConverter.ecef2lla(
            EcefLocation(
                position.location.ecefLocation.x,
                position.location.ecefLocation.y,
                position.location.ecefLocation.z
            )
        )
        val pvtLatLng =
            PvtFix(
                Location(
                    LlaLocation(
                        llaLocation.latitude,
                        llaLocation.longitude,
                        llaLocation.altitude
                    )
                ),
                position.time
            )

        // DOP computation
//        val gDop = sqrt(hgwMat[0, 0] + hgwMat[1, 1] + hgwMat[2, 2] + hgwMat[3, 3])
//        val pDop = sqrt(hgwMat[0, 0] + hgwMat[1, 1] + hgwMat[2, 2])
//        val tDop = sqrt(hgwMat[3, 3])
//        val dop = Dop(gDop, pDop, tDop)
        val dop = Dop()

        // Residue computation
        temp = doubleArrayOf()
        repeat(nSats) {
            temp += 0.0
        }
        val pEstMat = DMatrixRMaj.wrap(nSats, 1, temp)
        CommonOps_DDRM.mult(gMat, dMat, pEstMat)
        val resMat = DMatrixRMaj.wrap(nSats, 1, temp)
        CommonOps_DDRM.subtract(prMat, pEstMat, resMat)

        val residue = sqrt(resMat[0].pow(2) + resMat[1].pow(2) + resMat[2].pow(2))

//        response = ResponsePvtMultiConst(pvtLatLng.location.llaLocation, dop, residue, Corrections(), nSats.toFloat())
    }

    return response
}

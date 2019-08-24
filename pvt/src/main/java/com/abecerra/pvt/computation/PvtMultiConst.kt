package com.abecerra.pvt.computation

import com.abecerra.pvt.computation.corrections.getCtrlCorr
import com.abecerra.pvt.computation.data.*
import com.abecerra.pvt.computation.utils.Constants
import com.abecerra.pvt.computation.utils.Constants.C
import com.abecerra.pvt.computation.utils.Constants.GPS
import com.abecerra.pvt.computation.utils.Constants.PVT_ITER
import com.abecerra.pvt.computation.utils.CoordinatesConverter.ecef2lla
import com.abecerra.pvt.computation.utils.computeCNoWeightMatrix
import com.abecerra.pvt.computation.utils.outliers
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import kotlin.math.pow
import kotlin.math.sqrt

@Throws(Exception::class)
fun pvtMultiConst(gnssData: GnssData, computationSettings: ComputationSettings): ComputedPvtData? {
//
//    val responseList = arrayListOf<ComputedPvtData>()
//
//    val isMultiConst = computationSettings.constellations.contains(GPS) &&
//            computationSettings.constellations.contains(Constants.GALILEO)
//
//    gnssData.epochMeasurements.forEach { epoch ->
//
//        var position = EcefLocation()
//        var iono = arrayListOf<Double>()
//        var computedPvtData =
//            ComputedPvtData(PvtFix(Location(LlaLocation(), EcefLocation()), 0.0), ComputationSettings())
//
//        var nGps = 0
//        val gpsA = arrayListOf<ArrayList<Double>>()
//        val gpsP = arrayListOf<Double>()
//        val gpsTcorr = arrayListOf<Double>()
//        var gpsPcorr = arrayListOf<Double>()
//        val gpsX = arrayListOf<EcefLocation>()
//        val gpsPr = arrayListOf<Double>()
//        val gpsSvn = arrayListOf<Int>()
//        val gpsCn0 = arrayListOf<Double>()
//        val gpsSatellites = arrayListOf<SatelliteMeasurements>()
//        var gpsCorr: Double
//        var gpsPrC: Double
//        var gpsD0: Double
//        var gpsAx: Double
//        var gpsAy: Double
//        var gpsAz: Double
//
//
//        repeat(PVT_ITER) { i ->
//            //LS
//
//            //initialize iono
//            iono = epoch.ionoProto
//
//            if (i == 0) {
//                //Initialize to the ref position
//                position = EcefLocation(
//                    gnssData.refLocation.ecefLocation.x,
//                    gnssData.refLocation.ecefLocation.y,
//                    gnssData.refLocation.ecefLocation.z
//                )
//
//
//            }
//
//            //LOOP FOR GPS
//            if (computationSettings.constellations.contains(GPS)) {
//
//                if (i == 0) {
//                    gpsSatellites.addAll(epoch.satellitesMeasurements.filter { it.constellation == GPS })
//                    gpsSatellites.forEach {
//                        gpsPr.add(it.pR)
//                        gpsSvn.add(it.svid)
//                        gpsCn0.add(it.cn0)
//                    }
//                    nGps = gpsSatellites.size
//                }
//
//                gpsSatellites.forEachIndexed { j, gpsSat ->
//                    if (i == 0) {
//                        val ctrlCorr = getCtrlCorr(gpsSat, epoch.tow, gpsPr[j])
//                        gpsX.add(ctrlCorr.ecefLocation)
//                        gpsTcorr.add(ctrlCorr.tCorr)
//                    }
//
//
//
//                    gpsCorr = C * gpsTcorr[j]
//
//                    //Propagation corrections
////                    val propCorr =
////                        getPropCorr(gpsX[j], position, epoch.ionoProto, epoch.tow, computationSettings.corrections)
////                    gpsCorr = gpsCorr - propCorr.tropoCorr - propCorr.ionoCorr
//
//
//                    gpsPrC = gpsPr[j] + gpsCorr
//
//                    //gps GeometricMatrix
//                    if (gpsPrC != 0.0) {
//                        gpsD0 = sqrt(
//                            (gpsX[j].x - position.x).pow(2) +
//                                    (gpsX[j].y - position.y).pow(2) +
//                                    (gpsX[j].z - position.z).pow(2)
//                        )
//
//                        gpsP.add(j, gpsPrC - gpsD0)
//
//                        gpsAx = -(gpsX[j].x - position.x) / gpsD0
//                        gpsAy = -(gpsX[j].y - position.y) / gpsD0
//                        gpsAz = -(gpsX[j].z - position.z) / gpsD0
//
//                        val row = arrayListOf(gpsAx, gpsAy, gpsAz, 1.0)
//                        if (isMultiConst) row.add(0.0)
//                        gpsA.add(row)
//
//                    }
//
//                }
//
//                //pseudorange "RAIM"
//                val cleanSatsInd = outliers(gpsP)
//                cleanSatsInd.forEach {
//                    gpsP.removeAt(it)
//                    gpsA.removeAt(it)
//                    gpsCn0.removeAt(it)
//                }
//
//            }
//
//            //Least Squares
////            try {
////                if (isMultiConst) {
////                    val multiConstP = gpsP + galP
////                    val multiconstA = gpsA + galA
////                    val multiConstcn0 = gpsCn0 + galCn0
////
////                    responsePvtMultiConst =
////                        leastSquares(position, multiConstP, multiconstA, isMultiConst, multiConstcn0, isWeight)
////                } else {
////                    when {
////                        computationSettings.constellations.contains(GPS) -> {
////                            responsePvtMultiConst = leastSquares(position, gpsP, gpsA, isMultiConst, gpsCn0, isWeight)
////                        }
////                        computationSettings.constellations.contains(GALILEO) -> {
////                            responsePvtMultiConst = leastSquares(position, galP, galA, isMultiConst, galCn0, isWeight)
////                        }
////                    }
////                }
////            } catch (e: Exception) {
////                Timber.d(e.localizedMessage)
////            }
//
//        }

//        responseList.add(computedPvtData)

        return null

//
//    // Compute mean
//    val nEpoch = responseList.size
//    if (nEpoch > 0) {
//        val pvtLatLng = PvtFix(LlaLocation(0.0, 0.0, 0.0), 0.0)
//        val dop = Dop(0.0, 0.0, 0.0)
//        var residue = 0.0
//        var nSats = 0f
//        val corrections = Corrections(0.0, 0.0, 0.0, 0.0, 0.0)
//        responseList.forEach {
//            pvtLatLng.position.latitude += it.llaLocation.latitude
//            pvtLatLng.position.longitude += it.llaLocation.longitude
//            pvtLatLng.position.altitude += it.llaLocation.altitude
//            pvtLatLng.time += it.llaLocation.time
//
//            dop.gDop += it.dop.gDop
//            dop.pDop += it.dop.pDop
//            dop.tDop += it.dop.tDop
//
//            residue += it.residue
//
//            nSats += it.nSats
//        }
//        pvtLatLng.lat = pvtLatLng.lat / nEpoch
//        pvtLatLng.lng = pvtLatLng.lng / nEpoch
//        pvtLatLng.altitude = pvtLatLng.altitude / nEpoch
//        pvtLatLng.time = pvtLatLng.time / nEpoch
//
//        dop.gDop = dop.gDop / nEpoch
//        dop.pDop = dop.pDop / nEpoch
//        dop.tDop = dop.tDop / nEpoch
//
//        residue /= nEpoch
//
//        nSats /= nEpoch
//
//        pvtResponsePvtMultiConst = ResponsePvtMultiConst(pvtLatLng, dop, residue, corrections, nSats)
//    }

//    return ComputedPvtData(PvtFix(Location(LlaLocation(), EcefLocation()), 0.0), ComputationSettings())

}


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
        position.time += dArray[3] / C

        val ecefLocation = EcefLocation(
            position.location.ecefLocation.x,
            position.location.ecefLocation.y,
            position.location.ecefLocation.z
        )
        val llaLocation = ecef2lla(
            EcefLocation(
                position.location.ecefLocation.x,
                position.location.ecefLocation.y,
                position.location.ecefLocation.z
            )
        )
        val pvtLatLng =
            PvtFix(
                Location(LlaLocation(llaLocation.latitude, llaLocation.longitude, llaLocation.altitude)),
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
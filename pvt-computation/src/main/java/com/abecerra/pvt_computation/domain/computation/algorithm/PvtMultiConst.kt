package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.*
import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.output.Dop
import com.abecerra.pvt_computation.data.output.ResponsePvtMultiConst
import com.abecerra.pvt_computation.data.Constants.C
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.data.output.Corrections
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.leastSquares
import com.abecerra.pvt_computation.domain.computation.utils.outliers
import com.abecerra.pvt_computation.domain.corrections.getCtrlCorr
import kotlin.math.pow
import kotlin.math.sqrt

@Throws(Exception::class)
fun pvtMultiConst(pvtInputData: PvtInputData, computationSettings: ComputationSettings): PvtOutputData? {

    val responseList = arrayListOf<PvtOutputData>()

    val isMultiConst = computationSettings.constellations.contains(Constants.GPS) &&
            computationSettings.constellations.contains(Constants.GALILEO)

//    pvtInputData.epochMeasurements.forEach { epoch ->
//
        var position = EcefLocation()
        var iono = arrayListOf<Double>()

        var nGps = 0
        val gpsA = arrayListOf<ArrayList<Double>>()
        val gpsP = arrayListOf<Double>()
        val gpsTcorr = arrayListOf<Double>()
        var gpsPcorr = arrayListOf<Double>()
        val gpsX = arrayListOf<EcefLocation>()
        val gpsPr = arrayListOf<Double>()
        val gpsSvn = arrayListOf<Int>()
        val gpsCn0 = arrayListOf<Double>()
        val gpsSatellites = arrayListOf<SatelliteMeasurements>()
        var gpsCorr: Double
        var gpsPrC: Double
        var gpsD0: Double
        var gpsAx: Double
        var gpsAy: Double
        var gpsAz: Double


        repeat(Constants.PVT_ITER) { i ->
            //LS

            //initialize iono
            iono = epoch.ionoProto

            //LOOP FOR GPS
            if (computationSettings.constellations.contains(Constants.GPS)) {

                if (i == 0) {
                    gpsSatellites.addAll(epoch.satellitesMeasurements.filter { it.constellation == Constants.GPS })
                    gpsSatellites.forEach {
                        gpsPr.add(it.pR)
                        gpsSvn.add(it.svid)
                        gpsCn0.add(it.cn0)
                    }
                    nGps = gpsSatellites.size
                }

                gpsSatellites.forEachIndexed { j, gpsSat ->
                    if (i == 0) {
                        val ctrlCorr = getCtrlCorr(gpsSat, epoch.tow, gpsPr[j])
                        gpsX.add(ctrlCorr.ecefLocation)
                        gpsTcorr.add(ctrlCorr.tCorr)
                    }



                    gpsCorr = C * gpsTcorr[j]

                    //Propagation corrections
//                    val propCorr =
//                        getPropCorr(gpsX[j], position, epoch.ionoProto, epoch.tow, computationSettings.corrections)
//                    gpsCorr = gpsCorr - propCorr.tropoCorr - propCorr.ionoCorr


                    gpsPrC = gpsPr[j] + gpsCorr

                    //gps GeometricMatrix
                    if (gpsPrC != 0.0) {
                        gpsD0 = sqrt(
                            (gpsX[j].x - position.x).pow(2) +
                                    (gpsX[j].y - position.y).pow(2) +
                                    (gpsX[j].z - position.z).pow(2)
                        )

                        gpsP.add(j, gpsPrC - gpsD0)

                        gpsAx = -(gpsX[j].x - position.x) / gpsD0
                        gpsAy = -(gpsX[j].y - position.y) / gpsD0
                        gpsAz = -(gpsX[j].z - position.z) / gpsD0

                        val row = arrayListOf(gpsAx, gpsAy, gpsAz, 1.0)
                        if (isMultiConst) row.add(0.0)
                        gpsA.add(row)

                    }

                }

                //pseudorange "RAIM"
                val cleanSatsInd = outliers(gpsP)
                cleanSatsInd.forEach {
                    gpsP.removeAt(it)
                    gpsA.removeAt(it)
                    gpsCn0.removeAt(it)
                }

            }

            //Least Squares
            try {
                if (isMultiConst) {
                    val multiConstP = gpsP + galP
                    val multiconstA = gpsA + galA
                    val multiConstcn0 = gpsCn0 + galCn0

                    responsePvtMultiConst =
                        leastSquares(
                            position,
                            multiConstP,
                            multiconstA,
                            isMultiConst,
                            multiConstcn0,
                            isWeight
                        )
                } else {
                    when {
                        computationSettings.constellations.contains(Constants.GPS) -> {
                            responsePvtMultiConst =
                                leastSquares(
                                    position,
                                    gpsP,
                                    gpsA,
                                    isMultiConst,
                                    gpsCn0,
                                    isWeight
                                )
                        }
                        computationSettings.constellations.contains(Constants.GALILEO) -> {
                            responsePvtMultiConst =
                                leastSquares(
                                    position,
                                    galP,
                                    galA,
                                    isMultiConst,
                                    galCn0,
                                    isWeight
                                )
                        }
                    }
                }
            } catch (e: Exception) {
//                Timber.d(e.localizedMessage)
            }

        }

        responseList.add(computedPvtData)

        return null
    }


    // Compute mean
    val nEpoch = responseList.size
    if (nEpoch > 0) {
        val pvtLatLng = PvtFix(LlaLocation(0.0, 0.0, 0.0), 0.0)
        val dop = Dop(0.0, 0.0, 0.0)
        var residue = 0.0
        var nSats = 0f
        val corrections = Corrections(0.0, 0.0, 0.0, 0.0, 0.0)
        responseList.forEach {
            pvtLatLng.position.latitude += it.llaLocation.latitude
            pvtLatLng.position.longitude += it.llaLocation.longitude
            pvtLatLng.position.altitude += it.llaLocation.altitude
            pvtLatLng.time += it.llaLocation.time

            dop.gDop += it.dop.gDop
            dop.pDop += it.dop.pDop
            dop.tDop += it.dop.tDop

            residue += it.residue

            nSats += it.nSats
        }
        pvtLatLng.lat = pvtLatLng.lat / nEpoch
        pvtLatLng.lng = pvtLatLng.lng / nEpoch
        pvtLatLng.altitude = pvtLatLng.altitude / nEpoch
        pvtLatLng.time = pvtLatLng.time / nEpoch

        dop.gDop = dop.gDop / nEpoch
        dop.pDop = dop.pDop / nEpoch
        dop.tDop = dop.tDop / nEpoch

        residue /= nEpoch

        nSats /= nEpoch

        pvtResponsePvtMultiConst = ResponsePvtMultiConst(pvtLatLng, dop, residue, corrections, nSats)
    }

    return null
}



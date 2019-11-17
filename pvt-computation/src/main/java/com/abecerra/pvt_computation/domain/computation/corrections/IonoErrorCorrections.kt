package com.abecerra.pvt_computation.domain.computation.corrections


import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.PvtConstants.C
import com.abecerra.pvt_computation.data.PvtConstants.KLOBUCHAR
import com.abecerra.pvt_computation.domain.computation.utils.CoordinatesConverter
import java.lang.Math.*
import kotlin.math.pow


/**
 * Computation of the pseudorange correction due to ionospheric delay.
 * At this moment, only Klobuchar model is used
 */
fun ionoErrorCorrections(
    llaRefPos: LlaLocation,
    topoSatPos: CoordinatesConverter.Topocentric,
    timeRx: Double,
    iono: ArrayList<Double>,
    ionoModel: Int
): Double {

    var corr = 0.0

    if (ionoModel == KLOBUCHAR) {
        corr = klobucharModel(
            llaRefPos.latitude,
            llaRefPos.longitude,
            topoSatPos.azimuth,
            topoSatPos.elevation,
            timeRx,
            iono
        )
    }

    return corr
}


/**
 * KLOBUCHAR MODEL
 * Algorithm taken from Leick, A. (2004) "GPS Satellite Surveying - 2nd Edition"
 * John Wiley & Sons, Inc., New York, pp. 301-303)
 */
fun klobucharModel(
    latitude: Double,
    longitude: Double,
    azimuth: Double,
    elevation: Double,
    timeRx: Double,
    ionoParams: ArrayList<Double>
): Double {
    var delay = 0.0

    // Ionospheric parameters
    val a0 = ionoParams[0]
    val a1 = ionoParams[1]
    val a2 = ionoParams[2]
    val a3 = ionoParams[3]
    val b0 = ionoParams[4]
    val b1 = ionoParams[5]
    val b2 = ionoParams[6]
    val b3 = ionoParams[7]

    // Elevation from 0 to 90 degrees
    var el = abs(elevation)

    // Conversion to semicircles
    val lat = latitude / 180
    val lon = longitude / 180
    val az = azimuth / 180
    el /= 180

    val f = 1 + 16 * pow((0.53 - el), 3.0)

    val psi = (0.0137 / (el + 0.11)) - 0.022

    var phi = lat + psi * cos(az * PI)
    if (phi > 0.416) phi = 0.416
    if (phi < -0.416) phi = -0.416

    val lambda = lon + ((psi * sin(az * PI)) / cos(phi * PI))

    val ro = phi + 0.064 * cos((lambda - 1.617) * PI)

    var t = lambda * 43200 + timeRx
    t = t.rem(86400)

    var a = a0 + a1 * ro + a2 * ro.pow(2.0) + a3 * ro.pow(3.0)
    if (a < 0) a = 0.0

    var p = b0 + b1 * ro + b2 * ro.pow(2.0) + b3 * ro.pow(3.0);
    if (p < 72000) p = 72000.0

    val x = (2 * PI * (t - 50400)) / p

    // ionospheric delay
    if (abs(x) < 1.57) {
        delay = C * f * (5e-9 + a * (1 - (x.pow(2.0)) / 2 + (x.pow(4)) / 24))
    } else {
        delay = C * f * 5e-9
    }

    return delay
}

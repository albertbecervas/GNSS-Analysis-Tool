package com.abecerra.pvt_computation.domain.computation.utils

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.output.PvtEcef
import com.abecerra.pvt_computation.data.output.PvtLatLng
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.mult.MatrixVectorMult_DDRM
import java.lang.Math.*


object CoordinatesConverter {
    // WGS84 ellipsoid constants
    private const val a = 6378137.0 // radius
    private const val e = 8.1819190842622e-2  // eccentricity

    private val asq = Math.pow(a, 2.0)
    private val esq = Math.pow(e, 2.0)


    class Geodetic(
        var dphi: Double = 0.0,
        var dlambda: Double = 0.0,
        var h: Double = 0.0
    )

    class Topocentric(
        var azimuth: Double = 0.0,
        var elevation: Double = 0.0,
        var distance: Double = 0.0
    )

    /**
     * Convert location object on Earth-centered Earth-fixed to geodetic coordinates
     * Reference system: WGS84
     */
    fun ecef2lla(ecefLocation: EcefLocation): LlaLocation {
        val x = ecefLocation.x
        val y = ecefLocation.y
        val z = ecefLocation.z

        val b = Math.sqrt(asq * (1 - esq))
        val bsq = Math.pow(b, 2.0)
        val ep = Math.sqrt((asq - bsq) / bsq)
        val p = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0))
        val th = Math.atan2(a * z, b * p)

        var lon = Math.atan2(y, x)
        var lat =
            Math.atan2(
                z + Math.pow(ep, 2.0) * b * Math.pow(Math.sin(th), 3.0),
                p - esq * a * Math.pow(Math.cos(th), 3.0)
            )
        val N = a / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2.0))
        val alt = p / Math.cos(lat) - N

        // mod lat to 0-2pi
        lon %= (2 * Math.PI)

        lat = Math.toDegrees(lat)
        lon = Math.toDegrees(lon)

        return LlaLocation(lat, lon, alt)
    }

    /**
     * Convert location object on geodetic coordinates to Earth-centered Earth-fixed
     * Reference system: WGS84
     */
    fun lla2ecef(llaLocation: LlaLocation): EcefLocation {
        var lat = llaLocation.latitude ?: 0.0
        var lon = llaLocation.longitude ?: 0.0
        val alt = llaLocation.altitude ?: 0.0

        lat = Math.toRadians(lat)
        lon = Math.toRadians(lon)


        val n = a / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2.0))

        val x = (n + alt) * Math.cos(lat) * Math.cos(lon)
        val y = (n + alt) * Math.cos(lat) * Math.sin(lon)
        val z = ((1 - esq) * n + alt) * Math.sin(lat)

        return EcefLocation(x, y, z)
    }

    /**
     * Subroutine to calculate geodetic coordinates latitude, longitude, height given Cartesian coordinates X,Y,Z, and
     * reference ellipsoid values semi-major axis (a) and the inverse of flattening (fInv).
     *
     * The units of linear parameters X,Y,Z,a must all agree (m,km,mi,ft,..etc).
     * The output units of angular quantities will be in decimal degrees (15.5 degrees not 15 deg 30 min).
     * The output units of h will be the same as the units of X,Y,Z,a.
     */

    fun toGeod(a: Double, fInv: Double, x: Double, y: Double, z: Double): Geodetic {

        var h: Double
        val tolSq = 1E-10
        val maxIt = 10

        // Compute square of eccentricity
        val esq = if (fInv < 1E-20) {
            0.0
        } else {
            (2 - 1 / fInv) / fInv
        }

        val oneEsq = 1 - esq

        // First guess
        // P is distance from spin axis
        val p = sqrt(pow(x, 2.0) + pow(y, 2.0))

        var dLambda = if (p > 1E-20) {
            toDegrees(atan2(y, x))
        } else {
            0.0
        }

        if (dLambda < 0.0) {
            dLambda += 360
        }

        // r is distance from origin (0,0,0)
        val r = sqrt(pow(p, 2.0) + pow(z, 2.0))
        var sinPhi = if (r > 1E-20) {
            z / r
        } else {
            0.0
        }

        var dPhi = asin(sinPhi)
        // Initial value of height = distance from origin minus approximate
        // distance from origin to surface of ellipsoid
        if (r < 1E-20) {
            h = 0.0
        } else {
            h = r - a * (1 - sinPhi * sinPhi / fInv)
            var dP: Double
            var dZ: Double
            var i = 0
            // Iterate while not converges
            do {
                sinPhi = sin(dPhi)
                val cosPhi = cos(dPhi)

                // Compute radius of curvature in prime vertical direction
                val nPhi = a / sqrt(1 - esq * sinPhi * sinPhi)

                // Compute residuals in p and z
                dP = p - (nPhi + h) * cosPhi
                dZ = z - (nPhi * oneEsq + h) * sinPhi

                // Update height and latitude
                h += (sinPhi * dZ + cosPhi * dP)
                dPhi += (cosPhi * dZ - sinPhi * dP) / (nPhi + h)

                i++
            } while ((dP * dP + dZ * dZ > tolSq) && (i < maxIt))
            dPhi = toDegrees(dPhi)

            if (i == maxIt) {
//                Timber.d("Problem in toGeod, did not converge in $i iterations")
            }
        }

        return Geodetic(dPhi, dLambda, h)
    }

    /**
     * Transformation of vector dx into topocentric coordinate system with origin at X.
     * Both parameters are 3 by 1 vectors.
     */
    fun toTopocent(xArray: DoubleArray, dxArray: DoubleArray): Topocentric {
        var az: Double
        val el: Double

        val geod = toGeod(6378137.0, 298.257223563, xArray[0], xArray[1], xArray[2])

        val cl = cos(toRadians(geod.dlambda))
        val sl = sin(toRadians(geod.dlambda))
        val cb = cos(toRadians(geod.dphi))
        val sb = sin(toRadians(geod.dphi))

        val matrixArray = doubleArrayOf(
            -sl, -sb * cl, cb * cl,  // 1st row
            cl, -sb * sl, cb * sl,   // 2nd row
            0.0, cb, sb              // 3rd row
        )

        val fMat = DMatrixRMaj.wrap(3, 3, matrixArray)
        val dxVec = DMatrixRMaj.wrap(1, 3, dxArray)
        val localVec = DMatrixRMaj.wrap(1, 3, doubleArrayOf(0.0, 0.0, 0.0))


        //localVec = fMat' * dxVec
        MatrixVectorMult_DDRM.multTransA_small(fMat, dxVec, localVec)

        val e = localVec[0]
        val n = localVec[1]
        val u = localVec[2]

        val horDis = sqrt(e * e + n * n)

        if (horDis < 1e-20) {
            az = 0.0
            el = 90.0
        } else {
            az = toDegrees(atan2(e, n))
            el = toDegrees(atan2(u, horDis))
        }
        if (az < 360.0) {
            az += 360
        }

        val d = sqrt(pow(dxArray[0], 2.0) + pow(dxArray[1], 2.0) + pow(dxArray[2], 2.0))

        return Topocentric(az, el, d)
    }

    fun pvtEcef2PvtLla(pvtEcef: PvtEcef): PvtLatLng {
        val posEcef = EcefLocation(pvtEcef.x, pvtEcef.y, pvtEcef.z)
        val posLla = ecef2lla(posEcef)
        return PvtLatLng(posLla.latitude, posLla.longitude, posLla.altitude, pvtEcef.clockBias)
    }

    fun pvtLla2PvtEcef(pvtLatLng: PvtLatLng): EcefLocation {
        val posLatLng = LlaLocation(pvtLatLng.lat, pvtLatLng.lng, pvtLatLng.altitude)
        val posEcef = lla2ecef(posLatLng)
        return EcefLocation(posEcef.x, posEcef.y, posEcef.z)
    }
}


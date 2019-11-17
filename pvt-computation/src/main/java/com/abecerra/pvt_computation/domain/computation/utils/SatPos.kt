package com.abecerra.pvt_computation.domain.computation.utils


import com.abecerra.pvt_computation.data.PvtConstants
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.abecerra.pvt_computation.data.PvtConstants.GM
import com.abecerra.pvt_computation.data.PvtConstants.GM_GAL
import com.abecerra.pvt_computation.data.PvtConstants.GM_GPS
import com.abecerra.pvt_computation.data.PvtConstants.GPS
import com.abecerra.pvt_computation.data.PvtConstants.OMEGA_EARTH_DOT
import kotlin.math.*

fun satPos(tRx: Double, satellite: SatelliteMeasurements): SatPos {
    val satPos = doubleArrayOf(0.0, 0.0, 0.0)
    val satV = doubleArrayOf(0.0, 0.0, 0.0)

    val constellation = satellite.constellation

    with(satellite.satelliteEphemeris) {

        keplerModel?.let {

            val a = it.sqrtA.pow(2)
            val tk = checkTime(tRx - it.toeS)
            val n0 = if (constellation == GPS) sqrt(GM_GPS / a.pow(3)) else sqrt(GM_GAL / a.pow(3))
            val n = n0 + it.deltaN
            var m = it.m0 + n * tk
            m = (m + 2 * PI).rem(2 * PI)
            var e = m


            for (i in 0 until 10) {

                val eOld = e
                e = m + it.eccentricity * sin(e)
                val dE = (e - eOld).rem(2 * PI)
                if (abs(dE) < 1.0e-12) {
                    break
                }
            }

            e = (e + 2 * PI).rem(2 * PI)
            val v = atan2(sqrt(1 - it.eccentricity.pow(2)) * sin(e), cos(e) - it.eccentricity)
            var phi = v + it.omega
            phi = phi.rem(2 * PI)
            val u = phi + it.cuc * cos(2 * phi) + it.cus * sin(2 * phi)
            val r = a * (1 - it.eccentricity * cos(e)) + it.crc * cos(2 * phi) + it.crs * sin(2 * phi)
            val i = it.i0 + it.iDot * tk + it.cic * cos(2 * phi) + it.cis * sin(2 * phi)
            var omega = it.omega0 + (it.omegaDot - OMEGA_EARTH_DOT) * tk - OMEGA_EARTH_DOT * it.toeS
            omega = (omega + 2 * PI).rem(2 * PI)
            val x1 = cos(u) * r
            val y1 = sin(u) * r

            satPos[0] = x1 * cos(omega) - y1 * cos(i) * sin(omega)
            satPos[1] = x1 * sin(omega) + y1 * cos(i) * cos(omega)
            satPos[2] = y1 * sin(i)

            val eK = e
            val fK = v
            val phiK = phi
            val uK = u
            val x1K = x1
            val y1K = y1
            val iK = i
            val xK = satPos[0]
            val yK = satPos[1]

            val omegaK = omega
            val eKdot = n / (1 - it.eccentricity * cos(eK))
            val fKdot = sin(eK) * eKdot * (1 + it.eccentricity * cos(fK)) /
                    ((1 - cos(eK) * it.eccentricity) * sin(fK))
            val phiKdot = fKdot
            val uKdot = phiKdot + 2 * (it.cus * cos(2 * phiK) - it.cuc * sin(2 * phiK)) * phiKdot
            val rKdot = a * it.eccentricity * sin(eK) * eKdot +
                    2 * (it.crs * cos(2 * phiK) - it.crc * sin(2 * phiK)) * phiKdot
            val iKdot = it.iDot + 2 * (it.cis * cos(2 * phiK) - it.cic * sin(2 * phiK)) * phiKdot
            val omegaKdot = it.omegaDot - OMEGA_EARTH_DOT
            val x1kDot = rKdot * cos(uK) - y1K * uKdot
            val y1kDot = rKdot * sin(uK) + x1K * uKdot
            val xkDot =
                x1kDot * cos(omegaK) - y1kDot * cos(iK) * sin(omegaK) + y1K * sin(iK) * sin(omegaK) * iKdot - yK * omegaKdot
            val ykDot =
                x1kDot * sin(omegaK) + y1kDot * cos(iK) * cos(omegaK) - y1K * sin(iK) * cos(omegaK) * iKdot + xK * omegaKdot

            val zkDot = y1kDot * sin(iK) + y1K * cos(iK) * iKdot

            satV[0] = xkDot
            satV[1] = ykDot
            satV[2] = zkDot

        }

    }

    return SatPos(satPos, satV)
}

data class SatPos(
    var x: DoubleArray = doubleArrayOf(),
    var vel: DoubleArray = doubleArrayOf()
)

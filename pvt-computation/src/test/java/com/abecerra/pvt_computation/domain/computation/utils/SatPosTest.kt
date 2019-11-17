package com.abecerra.pvt_computation.domain.computation.utils

import com.abecerra.pvt_computation.data.ephemeris.KeplerianModel
import com.abecerra.pvt_computation.data.input.SatelliteEphemeris
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.google.gson.Gson
import org.junit.Test

class SatPosTest {

    @Test
    fun testSatellitePosition() {
        val satPos = satPos(
            1.3933e5, SatelliteMeasurements(
                1,
                1,
                2,
                3,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9,
                10,
                SatelliteEphemeris(
                    1,
                    7200.0,
                    2057,
                    0.0,
                    0.0,
                    15.0,
                    16.0,
                    KeplerianModel(
                        toeS = 144000.0,
                        deltaN = 4.345538151963753e-09,
                        m0 = -2.851940672902162,
                        eccentricity = 0.018924818490632,
                        sqrtA = 5.153678335189819e+03,
                        omegaDot = -7.638889618985842e-09,
                        omega = -1.756671707742028,
                        omega0 = -1.918848428767706,
                        iDot = 4.253748614275359e-10,
                        i0 = 0.953742579229515,
                        cic = 2.533197402954102e-07,
                        cis = -3.352761268615723e-08,
                        crc = 1.889687500000000e+02,
                        crs = -23.031250000000000,
                        cuc = -1.097097992897034e-06,
                        cus = 9.521842002868652e-06
                    )
                )
            )
        )

        val expectedResult = SatPos(
            doubleArrayOf(6556116.944122086, 1.8441372534883283E7, 1.8637386303008005E7),
            doubleArrayOf(-2043.0408304074117, -928.1697755462083, 1676.2096186551917)
        )

        assert(Gson().toJson(expectedResult).equals(Gson().toJson(satPos)))

    }

}
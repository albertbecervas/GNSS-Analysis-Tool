package com.abecerra.pvt_computation.domain.computation.corrections

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.ephemeris.KeplerianModel
import com.abecerra.pvt_computation.data.input.SatelliteEphemeris
import com.abecerra.pvt_computation.data.input.SatelliteMeasurements
import com.google.gson.Gson
import org.junit.Test

class GetCtrlCorrTest {

    @Test
    fun testGetCtrlCorr() {
        val ctrlCorr = getCtrlCorr(
            SatelliteMeasurements(
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
                    144000.0,
                    2057,
                    -1.724963076412678e-4,
                    -9.549694368615746e-12,
                    0.0,
                    -2.048909664154053e-8,
                    KeplerianModel(
                        toeS = 144000.0,
                        deltaN = 4.345538151963753e-09,
                        m0 = -2.851940672902162,
                        eccentricity = 0.018924818490632,
                        sqrtA = 5.153678335189819e+03,
                        omegaDot = -7.638889618985842e-09,
                        omega =-1.756671707742028,
                        omega0 =-1.918848428767706,
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
            ), 139333.0, 2.339905407194209e7
        )

        val expectedResult = CtrlCorr(
            EcefLocation(6550250.649082417,
                1.8438623212876264E7,
                1.864228278440872E7),
            -1.7244745560216913E-4
        )

        assert(Gson().toJson(expectedResult).equals(Gson().toJson(ctrlCorr)))
    }

}
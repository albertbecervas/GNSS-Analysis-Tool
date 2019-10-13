package com.abecerra.pvt_computation.domain.computation.utils

object Constants {

    const val C = 299792458L
    const val OMEGA_EARTH_DOT = 7.2921151467e-5
    const val GM_GPS = 3.986005e14
    const val GM_GAL = 3.986004418e14
    const val GM = 3.986008e14
    const val HALF_WEEK = 302400
    //Standard atmosphere - Berg, 1948 (Bernese)
    const val PRESSURE = 1013.25 //Pressure [mbar]
    const val TEMPERATURE = 291.15 //Temperature [K]

    const val L1_FREQ = 157542000000.0
    const val L5_FREQ = 117645000000.0

    const val ALG_LS: Int = 1
    const val ALG_WLS: Int = 2

    const val CORR_IONOSPHERE: Int = 1
    const val CORR_TROPOSPHERE: Int = 2
    const val CORR_IONOFREE: Int = 3

    const val PVT_ITER = 5

    // Iono Models
    const val KLOBUCHAR = 1
    const val NE_QUICK = 2

    //Modes constants
    const val CN0_MASK = 1
    const val ELEVATION_MASK = 2

    const val GPS = 1
    const val GALILEO = 2

    const val GPS_KEY = "gps"
    const val GALILEO_E1_KEY = "galileo_E1"
    const val GALILEO_E5A_KEY = "galileo_E5a"

    const val L1 = 1
    const val L5 = 2
    const val E1 = 3
    const val E5A = 4

}

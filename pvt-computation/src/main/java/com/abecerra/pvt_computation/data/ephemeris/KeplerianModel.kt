package com.abecerra.pvt_computation.data.ephemeris

data class KeplerianModel(
    /** Time of ephemeris (Toe), in time of week (seconds)  */
    val toeS: Double,
    /** Mean motion difference from computed value (radians/sec)  */
    val deltaN: Double,
    /** Mean anomaly at reference time (radians)  */
    val m0: Double,
    /** Eccentricity (dimensionless)  */
    val eccentricity: Double,
    /** Square root of semi-major axis (meters^1/2)  */
    val sqrtA: Double,
    /** Rate of right ascension (radians/sec)  */
    val omegaDot: Double,
    /** Argument of perigee (radians)  */
    val omega: Double,
    /** Longitude of ascending node at weekly epoch (radians)  */
    val omega0: Double,
    /** Rate of inclination angle (radians/sec)  */
    val iDot: Double,
    /** Inclination angle at reference time (radians)  */
    val i0: Double,
    /** Amplitude of cosine harmonic correction term to angle of inclination  */
    val cic: Double,
    /** Amplitude of sine harmonic correction term to angle of inclination  */
    val cis: Double,
    /** Amplitude of cosine harmonic correction term to the orbit radius  */
    val crc: Double,
    /** Amplitude of sine harmonic correction term to the orbit radius  */
    val crs: Double,
    /** Amplitude of cosine harmonic correction term to the argument of latitude  */
    val cuc: Double,
    /** Amplitude of sine harmonic correction term to the argument of latitude  */
    val cus: Double
)

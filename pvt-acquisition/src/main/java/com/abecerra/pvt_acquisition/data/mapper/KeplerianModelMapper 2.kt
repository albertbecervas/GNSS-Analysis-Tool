package com.abecerra.pvt_acquisition.data.mapper

import com.abecerra.pvt_computation.data.ephemeris.KeplerianModel

object KeplerianModelMapper {

    fun mapKeplerianModel(
        from: com.abecerra.pvt_ephemeris_client.suplclient.ephemeris.KeplerianModel
    ): KeplerianModel {
        return with(from) {
            KeplerianModel(
                toeS, deltaN, m0, eccentricity, sqrtA, omegaDot, omega, omega0, iDot, i0, cic, cis,
                crc, crs, cuc, cus
            )
        }
    }

}

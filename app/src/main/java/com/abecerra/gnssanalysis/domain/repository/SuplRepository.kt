package com.abecerra.gnssanalysis.domain.repository

import com.google.android.gms.maps.model.LatLng

interface SuplRepository {

    fun getEphemerisData(refPos: LatLng)

}
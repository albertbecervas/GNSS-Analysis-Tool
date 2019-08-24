package com.abecerra.gnssanalysis.core.computation.data.mapper

import com.abecerra.gnssanalysis.core.base.BaseMapper
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.data.Location
import com.google.android.gms.maps.model.LatLng

object LatLngMapper : BaseMapper<Location, LatLng>() {
    override fun map(from: Location): LatLng {
        return with(from) {
            mapLlaLocation(llaLocation)
        }
    }

    private fun mapLlaLocation(from: LlaLocation): LatLng {
        return with(from) {
            LatLng(latitude, longitude)
        }
    }

}
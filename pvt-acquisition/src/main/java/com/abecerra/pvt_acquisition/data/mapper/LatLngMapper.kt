package com.abecerra.pvt_acquisition.data.mapper

import com.abecerra.pvt_computation.domain.computation.data.LlaLocation
import com.abecerra.pvt_computation.domain.computation.data.Location
import com.abecerra.pvt_acquisition.app.base.BaseMapper
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

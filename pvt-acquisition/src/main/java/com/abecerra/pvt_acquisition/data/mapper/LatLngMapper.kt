package com.abecerra.pvt_acquisition.data.mapper

import com.abecerra.pvt_acquisition.app.base.BaseMapper
import com.abecerra.pvt_computation.data.output.PvtLatLng
import com.google.android.gms.maps.model.LatLng

object LatLngMapper : BaseMapper<PvtLatLng, LatLng>() {
    override fun map(from: PvtLatLng): LatLng {
        return with(from) {
            mapLlaLocation(from)
        }
    }

    private fun mapLlaLocation(from: PvtLatLng): LatLng {
        return with(from) {
            LatLng(lat, lng)
        }
    }
}

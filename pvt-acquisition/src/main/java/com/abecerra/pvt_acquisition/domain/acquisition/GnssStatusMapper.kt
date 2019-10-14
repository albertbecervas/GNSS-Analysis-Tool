package com.abecerra.pvt_acquisition.domain.acquisition

import android.annotation.SuppressLint
import android.location.GnssStatus
import com.abecerra.pvt_computation.data.input.Epoch
import kotlin.math.roundToInt

@SuppressLint("WrongConstant")
fun Epoch.mapGnssStatus(gnssStatus: GnssStatus) {
    with(gnssStatus) {
        kotlin.repeat(satelliteCount) { i ->
            satellitesMeasurements.find { it.svid == getSvid(i) }?.let {
                it.constellation = getConstellationType(i)
                it.azimuth = getAzimuthDegrees(i).roundToInt()
                it.elevation = getElevationDegrees(i).roundToInt()
            }
        }
    }
}

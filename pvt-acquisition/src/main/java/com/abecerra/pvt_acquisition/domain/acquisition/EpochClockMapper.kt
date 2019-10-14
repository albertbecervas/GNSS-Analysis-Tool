package com.abecerra.pvt_acquisition.domain.acquisition

import android.location.GnssClock
import com.abecerra.pvt_computation.data.input.Epoch
import com.abecerra.pvt_computation.utils.PvtConstants
import kotlin.math.floor


fun Epoch.mapClockMeasurements(clock: GnssClock) {
    with(clock) {
        timeNanosGnss = this.timeNanos - (this.getBiasNanosOrZero() + this.fullBiasNanos)
        tow = floor(timeNanosGnss.rem(PvtConstants.WEEK_NANOS)) / 1e9
        now = floor(timeNanosGnss / PvtConstants.WEEK_NANOS)
    }
}

private fun GnssClock.getBiasNanosOrZero(): Double = if (hasBiasNanos()) biasNanos else 0.0

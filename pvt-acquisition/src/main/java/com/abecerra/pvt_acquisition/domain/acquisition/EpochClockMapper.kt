package com.abecerra.pvt_acquisition.domain.acquisition

import android.location.GnssClock
import com.abecerra.pvt_computation.data.PvtConstants.WEEK_NANOS
import com.abecerra.pvt_computation.data.input.Epoch
import kotlin.math.floor


fun Epoch.mapClockMeasurements(clock: GnssClock) {
    with(clock) {
        timeNanosGnss = this.timeNanos - (this.getBiasNanosOrZero() + this.fullBiasNanos)

        tow = floor(timeNanosGnss.rem(WEEK_NANOS) / 1000000000)
        now = floor(timeNanosGnss / WEEK_NANOS)
    }
}

private fun GnssClock.getBiasNanosOrZero(): Double = if (hasBiasNanos()) biasNanos else 0.0

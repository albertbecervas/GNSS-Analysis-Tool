package com.abecerra.pvt_acquisition.app.logger

import android.location.GnssMeasurementsEvent
import java.io.File

interface GnssMeasLogger {

    fun startNewLog()

    fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent)

    fun closeLoggerAndReturnFile(): File?

}

package com.abecerra.gnssanalysis.core.logger

import android.location.GnssClock
import android.location.GnssMeasurement
import android.location.GnssMeasurementsEvent
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import com.abecerra.gnssanalysis.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GnssMeasLogger {

    private var currentFile: File? = null

    private var mFileWriter: FileWriter? = null

    fun startNewLog() {

        val baseDirectory = File(Environment.getExternalStorageDirectory(), APP_ROOT)
        baseDirectory.mkdirs()

        val formatter = SimpleDateFormat("yyy_MM_dd_HH_mm_ss", Locale.ENGLISH)
        val now = Date()
        val fileName = String.format("%s_%s.txt", FILE_PREFIX, formatter.format(now))
        currentFile = File(baseDirectory, fileName)
        try {
            mFileWriter = FileWriter(currentFile)
        } catch (e: IOException) {
            Timber.e("Could not create file.")
        }

        // initialize the contents of the file
        try {
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("Header Description:")
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write(VERSION_TAG)
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val fileVersion = (BuildConfig.VERSION_NAME
                    + " Platform: "
                    + Build.VERSION.RELEASE
                    + " "
                    + "Manufacturer: "
                    + manufacturer
                    + " "
                    + "Model: "
                    + model)
            mFileWriter?.write(fileVersion)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write(
                "Raw,ElapsedRealtimeMillis,TimeNanos,LeapSecond,TimeUncertaintyNanos,FullBiasNanos,"
                        + "BiasNanos,BiasUncertaintyNanos,DriftNanosPerSecond,DriftUncertaintyNanosPerSecond,"
                        + "HardwareClockDiscontinuityCount,Svid,TimeOffsetNanos,State,ReceivedSvTimeNanos,"
                        + "ReceivedSvTimeUncertaintyNanos,Cn0DbHz,PseudorangeRateMetersPerSecond,"
                        + "PseudorangeRateUncertaintyMetersPerSecond,"
                        + "AccumulatedDeltaRangeState,AccumulatedDeltaRangeMeters,"
                        + "AccumulatedDeltaRangeUncertaintyMeters,CarrierFrequencyHz,CarrierCycles,"
                        + "CarrierPhase,CarrierPhaseUncertainty,MultipathIndicator,SnrInDb,"
                        + "ConstellationType,AgcDb,CarrierFrequencyHz"
            )
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write(
                "Fix,Provider,Latitude,Longitude,Altitude,Speed,Accuracy,(UTC)TimeInMs"
            )
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("Nav,Svid,Type,Status,MessageId,Sub-messageId,Data(Bytes)")
            mFileWriter?.write("\n")
            mFileWriter?.write(COMMENT_START)
            mFileWriter?.write("\n")
        } catch (e: IOException) {
            return
        }
    }

    fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        if (mFileWriter == null) {
            return
        }
        val gnssClock = event.clock
        for (measurement in event.measurements) {
            try {
                writeGnssMeasurementToFile(gnssClock, measurement)
            } catch (e: IOException) {
            }
        }
    }

    @Throws(IOException::class)
    private fun writeGnssMeasurementToFile(clock: GnssClock, measurement: GnssMeasurement) {
        val clockStream = String.format(
            "Raw,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            SystemClock.elapsedRealtime(),
            clock.timeNanos,
            if (clock.hasLeapSecond()) clock.leapSecond else "",
            if (clock.hasTimeUncertaintyNanos()) clock.timeUncertaintyNanos else "",
            clock.fullBiasNanos,
            if (clock.hasBiasNanos()) clock.biasNanos else "",
            if (clock.hasBiasUncertaintyNanos()) clock.biasUncertaintyNanos else "",
            if (clock.hasDriftNanosPerSecond()) clock.driftNanosPerSecond else "",
            if (clock.hasDriftUncertaintyNanosPerSecond())
                clock.driftUncertaintyNanosPerSecond
            else "",
            clock.hardwareClockDiscontinuityCount.toString() + ","
        )
        mFileWriter?.write(clockStream)

        val measurementStream = String.format(
            "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
            measurement.svid,
            measurement.timeOffsetNanos,
            measurement.state,
            measurement.receivedSvTimeNanos,
            measurement.receivedSvTimeUncertaintyNanos,
            measurement.cn0DbHz,
            measurement.pseudorangeRateMetersPerSecond,
            measurement.pseudorangeRateUncertaintyMetersPerSecond,
            measurement.accumulatedDeltaRangeState,
            measurement.accumulatedDeltaRangeMeters,
            measurement.accumulatedDeltaRangeUncertaintyMeters,
            if (measurement.hasCarrierFrequencyHz()) measurement.carrierFrequencyHz else "",
            if (measurement.hasCarrierCycles()) measurement.carrierCycles else "",
            if (measurement.hasCarrierPhase()) measurement.carrierPhase else "",
            if (measurement.hasCarrierPhaseUncertainty())
                measurement.carrierPhaseUncertainty
            else "",
            measurement.multipathIndicator,
            if (measurement.hasSnrInDb()) measurement.snrInDb else "",
            measurement.constellationType,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && measurement.hasAutomaticGainControlLevelDb())
                measurement.automaticGainControlLevelDb
            else "",
            if (measurement.hasCarrierFrequencyHz()) measurement.carrierFrequencyHz else ""
        )
        mFileWriter?.write(measurementStream)
        mFileWriter?.write("\n")
    }

    fun closeLoggerAndReturnFile(): File? {
        mFileWriter?.close()
        return currentFile
    }

    companion object {
        const val APP_ROOT = "/GNSSAnalysis/Nmea/Logs/"
        const val FILE_PREFIX = "gnss_log"
        const val MAX_FILES_STORED = 100

        const val COMMENT_START = "# "
        const val VERSION_TAG = "Version: "
    }
}

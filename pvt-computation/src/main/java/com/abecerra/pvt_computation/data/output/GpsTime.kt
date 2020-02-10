package com.abecerra.pvt_computation.data.output

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*
import java.util.concurrent.TimeUnit


class GpsTime(private val gpsNanos: Long) {

    /**
     * @return a DateTime based on the pure GPS clockBias (without considering leap second).
     */
    private fun getGpsDateTime(): DateTime {
        return DateTime(TimeUnit.NANOSECONDS.toMillis(gpsNanos + GPS_UTC_EPOCH_OFFSET_NANOS), UTC_ZONE)
    }


    /**
     * Computes leap seconds. Only accurate after 2009.
     * @param time
     * @return number of leap seconds since GPS epoch.
     */
    private fun getLeapSecond(time: DateTime): Int {
        return when {
            LEAP_SECOND_DATE_2017 <= time -> 18
            LEAP_SECOND_DATE_2015 <= time -> 17
            LEAP_SECOND_DATE_2012 <= time -> 16
            LEAP_SECOND_DATE_1981 <= time -> // Only correct between 2012/7/1 to 2008/12/31
                15
            else -> 0
        }
    }


    /**
     * @return a DateTime with leap seconds considered.
     */
    fun getUtcDateTime(): Date {
        val gpsDateTime = getGpsDateTime()
        val time = gpsDateTime.millis - TimeUnit.SECONDS.toMillis(getLeapSecond(gpsDateTime).toLong())
        return Date(time)
    }

    companion object {
        // GPS epoch is 1980/01/06
        private const val GPS_DAYS_SINCE_JAVA_EPOCH: Long = 3657
        private val GPS_UTC_EPOCH_OFFSET_SECONDS = TimeUnit.DAYS.toSeconds(GPS_DAYS_SINCE_JAVA_EPOCH)
        private val GPS_UTC_EPOCH_OFFSET_NANOS = TimeUnit.SECONDS.toNanos(GPS_UTC_EPOCH_OFFSET_SECONDS)
        private val UTC_ZONE = DateTimeZone.UTC
        private val LEAP_SECOND_DATE_1981 = DateTime(1981, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2012 = DateTime(2012, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2015 = DateTime(2015, 7, 1, 0, 0, UTC_ZONE)
        private val LEAP_SECOND_DATE_2017 = DateTime(2017, 1, 1, 0, 0, UTC_ZONE)
    }
}

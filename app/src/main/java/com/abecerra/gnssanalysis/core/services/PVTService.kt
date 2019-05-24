package com.abecerra.gnssanalysis.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.abecerra.gnssanalysis.core.utils.extensions.checkPermission
import com.abecerra.gnssanalysis.presentation.MainActivity
import com.abecerra.pvt.computation.PVTEngine
import com.abecerra.pvt.computation.data.LlaLocation
import com.abecerra.pvt.computation.ephemeris.EphemerisClient

class PVTService : Service(), LocationListener, SensorEventListener {

    private var pvtEngine = PVTEngine()
    private var ephemerisClient = EphemerisClient()

    private var locationManager: LocationManager? = null

    private var gnssStatusListener: GnssStatus.Callback? = null
    private var gnssMeasurementsEventListener: GnssMeasurementsEvent.Callback? = null
    private var gnssNmeaMessageListener: OnNmeaMessageListener? = null

    private var broadcaster: LocalBroadcastManager? = null
    private var notificationManager: NotificationManager? = null


    private var isComputingPVT = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (broadcaster == null) {
            broadcaster = LocalBroadcastManager.getInstance(this@PVTService)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startGnss()
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        setNotification()

        startComputing()

        return START_STICKY
    }

    private fun setNotification() {
        val notifIntent = Intent(this, MainActivity::class.java)
        //        notifIntent.putExtra(TACKING_EXTRA, true)
        //        notifIntent.putExtra(ROUTE_DETAIL_EXTRA, routeDetail)
        notifIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notifIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifChannel()
        }

        val builder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            //            .setContentTitle(getString(R.string.app_name))
            //            .setContentText(getString(R.string.notification_content))
            //            .setBadgeIconType(R.drawable.ic_notif_logo)
            //            .setLargeIcon(
            //                BitmapFactory.decodeResource(resources,
            //                    R.drawable.ic_notif_logo)
            //            )
            //            .setSmallIcon(R.drawable.ic_notif_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_HIGH
        }

        val notification = builder.build()

        notification.contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                0
            )
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Route tracking",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(false)
//        baseContext?.resources?.getColor(R.color.accent, theme)?.let {
//            notificationChannel.lightColor = it
//        }
        notificationChannel.enableVibration(false)
        notificationManager?.createNotificationChannel(notificationChannel)

        val updatesNChannel = NotificationChannel(
            UPDATES_NOTIFICATION_CHANNEL_ID,
            "Route tracking updates",
            NotificationManager.IMPORTANCE_HIGH
        )
        updatesNChannel.enableLights(false)
//        baseContext?.resources?.getColor(R.color.accent, theme)?.let {
//            updatesNChannel.lightColor = it
//        }
        updatesNChannel.enableVibration(true)
//        updatesNChannel.vibrationPattern = longArrayOf(100L)
        notificationManager?.createNotificationChannel(updatesNChannel)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startGnss() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            gnssStatusListener = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                }

                override fun onStarted() {
                    super.onStarted()

                }

                override fun onStopped() {
                    super.onStopped()
                }
            }

            gnssMeasurementsEventListener = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(measurementsEvent: GnssMeasurementsEvent?) {
                    if (isComputingPVT) {
                        val position = pvtEngine.obtainPosition()
                        broadcaster?.sendBroadcast(
                            Intent().setAction(SEND_PVT_RESULT).putExtra(PVT_RESULT_EXTRA, position)
                        )
                    }

                }
            }

            gnssNmeaMessageListener = OnNmeaMessageListener { message, timestamp ->

            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE, this
            )
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE, this
            )

            locationManager?.registerGnssStatusCallback(gnssStatusListener)
            locationManager?.registerGnssMeasurementsCallback(gnssMeasurementsEventListener)
            locationManager?.addNmeaListener(gnssNmeaMessageListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopGnss() {
        locationManager?.unregisterGnssStatusCallback(gnssStatusListener)
        locationManager?.unregisterGnssMeasurementsCallback(gnssMeasurementsEventListener)
        locationManager?.removeNmeaListener(gnssNmeaMessageListener)

        locationManager?.removeUpdates(this)
    }


    fun startComputing() {
        ephemerisClient.getEphemerisData(LlaLocation(), {
            isComputingPVT = true
        }, {

        })
    }

    fun stopComputing() {
        isComputingPVT = false
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopGnss()
        }
        super.onDestroy()
    }

    companion object {
        private const val MIN_TIME = 1L
        private const val MIN_DISTANCE = 0.0F

        const val NOTIFICATION_CHANNEL_ID = ".core.services.PVTService.NOTIFICATION_ID"
        const val UPDATES_NOTIFICATION_CHANNEL_ID = ".core.services.PVTService.UPDATES_NOTIFICATION_CHANNEL_ID"


        const val SEND_PVT_RESULT: String = ".core.services.PVTService.SEND_PVT_RESULT"
        const val START_COMPUTING: String = ".core.services.PVTService.START_COMPUTING"
        const val STOP_COMPUTING: String = ".core.services.PVTService.STOP_COMPUTING"

        const val PVT_RESULT_EXTRA: String = ".core.services.PVTService.PVT_RESULT_EXTRA"
    }

}
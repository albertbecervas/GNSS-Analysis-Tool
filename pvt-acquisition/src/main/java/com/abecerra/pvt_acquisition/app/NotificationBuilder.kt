package com.abecerra.pvt_acquisition.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abecerra.pvt_acquisition.R

object NotificationBuilder {

    private const val NOTIFICATION_CHANNEL_ID = ".core.services.GnssService.NOTIFICATION_ID"

    private var notificationManager: NotificationManager? = null

    fun Context.buildGnssNotification(pendingIntent: PendingIntent?): Notification? {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            val builder = NotificationCompat
                .Builder(
                    this,
                    NOTIFICATION_CHANNEL_ID
                )
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Computing PVT...")
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

            pendingIntent?.let { builder.setContentIntent(it) }

            return builder.build()
        } catch (e: ClassNotFoundException) {
            Log.d("PVT-ACQUISITION", "Main Activity not found.")
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager?) {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Pvt Computing",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(false)
        notificationChannel.enableVibration(false)
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}

package com.abecerra.gnssanalysis.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.abecerra.gnssanalysis.presentation.MainActivity

object NotificationBuilder {

    private const val NOTIFICATION_CHANNEL_ID = ".core.services.PvtService.NOTIFICATION_ID"

    private var notificationManager: NotificationManager? = null

    fun Context.buildNotification(): Notification {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifIntent = Intent(this, MainActivity::class.java)
        notifIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notifIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val builder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
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

        return notification

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
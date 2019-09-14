package com.abecerra.gnssanalysis.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.main.MainActivity

object NotificationBuilder {

    private const val NOTIFICATION_CHANNEL_ID = ".core.services.GnssService.NOTIFICATION_ID"

    private var notificationManager: NotificationManager? = null

    fun Context.buildGnssNotification(): Notification {

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
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Computing PVT...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        return builder.build()
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

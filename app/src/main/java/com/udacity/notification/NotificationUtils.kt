package com.udacity.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, titleOfDownload: String, status: Int){

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("download-title", "$titleOfDownload")
    contentIntent.putExtra("download-status", "$status")
    val contentPendingIntent = PendingIntent.getActivities(
            applicationContext,
            NOTIFICATION_ID,
            arrayOf(contentIntent),
            PendingIntent.FLAG_UPDATE_CURRENT
    )


    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
    )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .addAction(R.drawable.ic_assistant_black_24dp, "Check Download", contentPendingIntent )
            .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}
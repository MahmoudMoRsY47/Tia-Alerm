package com.example.tiaalert.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tiaalert.MainActivity
import com.example.tiaalert.R

class AlarmWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var mediaPlayer: MediaPlayer? = null

    override fun doWork(): Result {
        val intervalMillis = inputData.getLong("interval", 60000L)

        // Start Alarm Sound in Loop
        mediaPlayer = MediaPlayer.create(applicationContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        sendNotification(applicationContext, "Alarm Ringing", "Time's up!")

        return Result.success() // Indicates the alarm has rung
    }

    override fun onStopped() {
        super.onStopped()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }


    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // Create Intent to Open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

}

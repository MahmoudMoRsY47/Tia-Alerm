package com.example.tiaalert.main.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tiaalert.MainActivity
import com.example.tiaalert.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private var ringtone: Ringtone? = null

    override fun doWork(): Result {
        val alarmName = inputData.getString("alarmName")
            ?: context.getString(R.string.without_name)  // Get the alarm name

        // Play Ringtone Sound
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        ringtone?.play()

        // Send Alarm Notification
        sendNotification(applicationContext, alarmName)

        // Stop the alarm after 10 min using a coroutine on the main thread
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000000) // Stop after 10 min
            stopAlarm()
        }

        return Result.success() // Indicates alarm has rung successfully
    }

    override fun onStopped() {
        super.onStopped()
        stopAlarm()
    }

    private fun stopAlarm() {
        ringtone?.stop()
    }

    private fun sendNotification(context: Context, alarmName: String) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)


        // Create Intent to Stop Alarm
        val stopIntent = Intent(context, StopAlarmReceiver::class.java).apply {
            putExtra("alarmName", alarmName)  // Pass the alarm name to stop it
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create Intent to Open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Alarm", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(
            context.getString(
                R.string.alarm,
                alarmName
            )
        )  // Show the alarm name in the title
            .setContentText(
                context.getString(
                    R.string.time_s_up,
                    alarmName
                )
            )  // Show the alarm name in the content
            .setSmallIcon(R.drawable.tia1).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).setAutoCancel(true).build()

        notificationManager.notify(1, notification)
    }
}

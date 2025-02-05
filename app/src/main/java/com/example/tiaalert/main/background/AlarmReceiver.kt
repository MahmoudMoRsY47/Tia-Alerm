package com.example.tiaalert.main.background

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tiaalert.MainActivity
import com.example.tiaalert.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private var ringtone: Ringtone? = null

    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val intervalMillis = intent?.getLongExtra("interval", 0L)
            val alarmName = intent?.getStringExtra("alarmName")
            val alarmId = intent?.getIntExtra("alarmId", 0)

            // Trigger the alarm (e.g., show a notification or play a sound)
            if (intervalMillis != null && intervalMillis > 0) {
                // Schedule the next alarm if needed
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("interval", intervalMillis)
                    putExtra("alarmName", alarmName)
                    putExtra("alarmId", alarmId)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId!!,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val nextTriggerTime = System.currentTimeMillis() + intervalMillis
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent
                )
            }
            val alarmUri = Uri.parse("android.resource://${context.packageName}/${R.raw.child}")
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()

            // Send Alarm Notification
            sendNotification(it, alarmName ?: context.getString(R.string.without_name))

            // Stop the alarm after 10 minutes using a coroutine
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000000) // Stop after a long period (e.g., 10 minutes)
                stopAlarm()
            }

        }
    }

    private fun stopAlarm() {
        ringtone?.stop()
    }

    private fun sendNotification(context: Context, alarmName: String) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // Create Intent to Open MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Tia Alarm", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.alarm, alarmName))
            .setContentText(context.getString(R.string.time_s_up, alarmName))
            .setSmallIcon(R.drawable.tia1).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).setAutoCancel(true).build()

        // Send the notification
        notificationManager.notify(1, notification)
    }
}



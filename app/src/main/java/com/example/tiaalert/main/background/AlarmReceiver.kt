package com.example.tiaalert.main.background

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tiaalert.AlarmDetailsActivity
import com.example.tiaalert.R
import com.example.tiaalert.main.AlarmRing
import com.example.tiaalert.main.model.AlarmsModel
import com.example.tiaalert.main.utils.fromJson
import com.example.tiaalert.main.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {


    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val alarm = intent?.getStringExtra("alarm")
            val alarmAfter = alarm?.fromJson<AlarmsModel>()
//            val intervalMillis = intent?.getLongExtra("interval", 0L)
//            val alarmName = intent?.getStringExtra("alarmName")
//            val alarmId = intent?.getIntExtra("alarmId", 0)

            // Trigger the alarm (e.g., show a notification or play a sound)
            if (alarmAfter?.intervalHours != null && alarmAfter.intervalHours > 0) {
                // Schedule the next alarm if needed
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("alarm", alarm)

                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmAfter.id,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val nextTriggerTime =
                    System.currentTimeMillis() + alarmAfter.intervalHours * 60 * 60 * 1000L
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent
                )
            }
            AlarmRing.play(context)

            // Send Alarm Notification
            sendNotification(it, alarmAfter)

            // Stop the alarm after 10 minutes using a coroutine
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000000) // Stop after a long period (e.g., 10 minutes)
                stopAlarm()
            }

        }
    }

    private fun stopAlarm() {
        AlarmRing.stop()
    }

    private fun sendNotification(context: Context, alarm: AlarmsModel?) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // **إنشاء Intent لفتح `AlarmDetailsActivity` وتمرير بيانات التنبيه**
        val intent = Intent(context, AlarmDetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alarm", alarm.toJson()) // تمرير بيانات التنبيه
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
            .setContentTitle(context.getString(R.string.alarm, alarm?.name))
            .setContentText(context.getString(R.string.time_s_up, alarm?.name))
            .setSmallIcon(R.drawable.tia1).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).setAutoCancel(true).build()

        // Send the notification
        notificationManager.notify(1, notification)
    }
}



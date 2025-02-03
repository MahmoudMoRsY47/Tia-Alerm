package com.example.tiaalert.main.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.Log
import androidx.work.WorkManager

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // Get the alarm name from the intent (optional, can be used for logging or debugging)
            val alarmName = intent?.getStringExtra("alarmName")

            // Stop the alarm sound and release resources
            // You can stop any ongoing alarm sound here if you are using MediaPlayer or any other mechanism.
            // For example:
            val mediaPlayer = MediaPlayer.create(
                it,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            mediaPlayer.stop()
            mediaPlayer.release()

            // Optionally, you can cancel the work if it's still running
            WorkManager.getInstance(it).cancelAllWorkByTag(alarmName ?: "Unknown Alarm")

            // You can also remove or update alarm data if needed (e.g., marking it as stopped).
            Log.d("StopAlarmReceiver", "Alarm stopped: $alarmName")
        }
    }
}


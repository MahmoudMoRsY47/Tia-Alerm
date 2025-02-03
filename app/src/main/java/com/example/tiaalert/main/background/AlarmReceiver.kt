package com.example.tiaalert.main.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val intervalMillis = intent?.getLongExtra("interval", 60000L) ?: 60000L
            val alarmName = intent?.getStringExtra("alarmName") ?: "Unnamed Alarm"


            val workRequest =
                PeriodicWorkRequestBuilder<AlarmWorker>(intervalMillis, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            "interval" to intervalMillis,
                            "alarmName" to alarmName  // Pass the alarm name here
                        )
                    )
                    .build()

            // Enqueue the work without unique name to allow multiple alarms
            WorkManager.getInstance(it).enqueue(workRequest)
        }
    }
}


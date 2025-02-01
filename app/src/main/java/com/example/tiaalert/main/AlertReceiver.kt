package com.example.tiaalert.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val intervalMillis = intent?.getLongExtra("interval", 60000L) // Default: 60 sec
            val workRequest =
                intervalMillis?.let { it1 ->
                    PeriodicWorkRequestBuilder<AlarmWorker>(it1, TimeUnit.MILLISECONDS)
                        .setInputData(workDataOf("interval" to intervalMillis))
                        .build()
                }

            workRequest?.let { it1 ->
                WorkManager.getInstance(it).enqueueUniquePeriodicWork(
                    "AlarmWorker", ExistingPeriodicWorkPolicy.UPDATE, it1
                )
            }
        }
    }
}

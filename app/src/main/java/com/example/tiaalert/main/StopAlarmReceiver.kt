package com.example.tiaalert.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.widget.Toast
import com.example.tiaalert.R

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val stopIntent = Intent(it, AlertReceiver::class.java)
            it.stopService(stopIntent)
        }
    }
}

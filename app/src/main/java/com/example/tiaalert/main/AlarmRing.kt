package com.example.tiaalert.main

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import com.example.tiaalert.R

object AlarmRing {
    private var ringtone: Ringtone? = null

    fun play(context: Context) {
        val alarmUri = Uri.parse("android.resource://${context.packageName}/${R.raw.child}")
        ringtone = RingtoneManager.getRingtone(context, alarmUri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone?.isLooping = true
        }
        ringtone?.play()
    }

    fun stop() {
        ringtone?.stop()
    }
}
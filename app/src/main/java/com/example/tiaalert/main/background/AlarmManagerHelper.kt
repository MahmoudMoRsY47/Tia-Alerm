package com.example.tiaalert.main.background

import android.content.Context
import com.example.tiaalert.main.model.AlarmsModel
import com.example.tiaalert.main.utils.fromJsonArray
import com.example.tiaalert.main.utils.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmManagerHelper(context: Context) {

    private val prefs = context.getSharedPreferences("alarms_prefs", Context.MODE_PRIVATE)

    fun saveAlarm(alarm: AlarmsModel) {
        CoroutineScope(Dispatchers.IO).launch {
            val alarms = getAlarms().toMutableList()
            alarms.add(alarm)
            prefs.edit().putString("alarms", alarms.toJson()).apply()
        }
    }

    fun getAlarms(): List<AlarmsModel> {
        val json = prefs.getString("alarms", null) ?: return emptyList()
        return json.fromJsonArray()
    }

    fun removeAlarm(id: String) {
        val alarms = getAlarms().filterNot { it.id == id }
        prefs.edit().putString("alarms", alarms.toJson()).apply()

    }
}




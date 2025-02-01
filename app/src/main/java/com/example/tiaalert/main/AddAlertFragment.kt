package com.example.tiaalert.main

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tiaalert.databinding.FragmentAddAlertBinding
import java.util.Calendar

class AddAlertFragment : Fragment() {

    private lateinit var binding: FragmentAddAlertBinding


    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAlertBinding.inflate(inflater, container, false)


        binding.setAlarmButton.setOnClickListener {
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute
            val interval = binding.intervalInput.text.toString().toIntOrNull() ?: 1

            scheduleAlarm(hour, minute, interval)
        }

        return binding.root
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(hour: Int, minute: Int, intervalSeconds: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java).apply {
            putExtra("interval", intervalSeconds * 1000L) // Convert to milliseconds
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1) // Schedule for next day if time passed
        }

        // Schedule Exact Alarm
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(requireContext(), "Alarm set for $hour:$minute, repeating every $intervalSeconds sec", Toast.LENGTH_SHORT).show()
    }}
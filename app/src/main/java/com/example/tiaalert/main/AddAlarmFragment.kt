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
import androidx.navigation.fragment.findNavController
import com.example.tiaalert.R
import com.example.tiaalert.databinding.FragmentAddAlarmBinding
import com.example.tiaalert.main.background.AlarmManagerHelper
import com.example.tiaalert.main.background.AlarmReceiver
import com.example.tiaalert.main.model.AlarmsModel
import com.example.tiaalert.main.utils.toVisible
import java.util.Calendar
import java.util.UUID

class AddAlarmFragment : Fragment() {

    private lateinit var binding: FragmentAddAlarmBinding
    private lateinit var alarmManagerHelper: AlarmManagerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAlarmBinding.inflate(inflater, container, false)
        alarmManagerHelper = AlarmManagerHelper(requireContext())
        setAnimation()
        handleClick()
        return binding.root

    }

    private fun setAnimation() {
        with(binding) {
            ivTia.toVisible()
            timePicker.toVisible()
            etNameLayout.toVisible()
            etIntervalLayout.toVisible()
            setAlarmButton.toVisible()
        }
    }

    private fun handleClick() {
        binding.setAlarmButton.setOnClickListener {
            if (binding.etName.text.toString().isEmpty() || binding.etInterval.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_you_must_enter_name_of_alarm_and_interval_time),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val alarmId = UUID.randomUUID().hashCode()
                val hour = binding.timePicker.hour
                val minute = binding.timePicker.minute
                val interval = binding.etInterval.text.toString().toInt()
                val name =
                    binding.etName.text.toString().ifEmpty { getString(R.string.without_name) }
                val alarm = AlarmsModel(alarmId, hour, minute, interval, name)

                alarmManagerHelper.saveAlarm(alarm)

                scheduleAlarm(alarm.hour, alarm.minute, alarm.intervalHours, alarm.name, alarmId)
                findNavController().popBackStack()
            }
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(
        hour: Int,
        minute: Int,
        intervalHours: Int,
        alarmName: String,
        alarmId: Int
    ) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("interval", intervalHours * 60 * 60 * 1000L) // Ensure correct interval
            putExtra("alarmName", alarmName) // Pass the alarm name
            putExtra("alarmId", alarmId) // Pass alarm ID
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarmId, // Unique for each alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
//            if (timeInMillis + intervalHours * 60 * 60 * 1000 <= System.currentTimeMillis()) {
//                add(
//                    Calendar.HOUR_OF_DAY,
//                    intervalHours
//                ) // Add interval if the time has already passed
//            }
        }

        // Set the alarm to repeat at the specified interval
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + intervalHours * 60 * 60 * 1000L, // Interval in milliseconds
            pendingIntent
        )

        Toast.makeText(
            requireContext(),
            getString(
                R.string.alarm_set_for_repeating_every_hour,
                hour.toString(),
                minute.toString(),
                intervalHours.toString()
            ),
            Toast.LENGTH_SHORT
        ).show()
    }
}

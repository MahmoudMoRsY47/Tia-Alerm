package com.example.tiaalert.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tiaalert.R
import com.example.tiaalert.databinding.FragmentAlarmsBinding
import com.example.tiaalert.main.adapter.AlarmAdapter
import com.example.tiaalert.main.background.AlarmManagerHelper
import com.example.tiaalert.main.background.AlarmReceiver
import com.example.tiaalert.main.utils.toGone
import com.example.tiaalert.main.utils.toVisible

class AlarmsFragment : Fragment() {

    private lateinit var binding: FragmentAlarmsBinding
    private lateinit var alarmManagerHelper: AlarmManagerHelper
    private val alarmAdapter by lazy {
        AlarmAdapter { id ->
            // Step 1: Cancel the scheduled alarm
            cancelScheduledAlarm(id)

            // Step 2: Remove the alarm from storage
            alarmManagerHelper.removeAlarm(id)

            // Step 3: Update the UI
            updateAlarmList()

            Toast.makeText(requireContext(), getString(R.string.alarm_removed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmManagerHelper = AlarmManagerHelper(requireContext())
        binding.rvAlarms.adapter = alarmAdapter
        handleClick()
    }

    private fun handleClick() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_alarmsFragment_to_addAlarmFragment)
        }

        binding.tvLanguage.setOnClickListener {
            changeLanguage(if (binding.tvLanguage.text.toString() == "English") "en" else "ar")
        }
    }

    private fun changeLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    override fun onResume() {
        super.onResume()
        updateAlarmList() // Refresh the list when the fragment resumes
    }

    private fun updateAlarmList() {
        binding.btnAdd.toVisible()
        binding.tvTia.toVisible()
        binding.tvLanguage.toVisible()
        if (alarmManagerHelper.getAlarms().isEmpty()) {
            binding.ivEmpty.toVisible()
            binding.tvEmpty.toVisible()
            binding.rvAlarms.toGone()
            binding.ivTitle.toGone()
        } else {
            binding.ivEmpty.toGone()
            binding.tvEmpty.toGone()
            binding.rvAlarms.toVisible()
            binding.ivTitle.toVisible()
            binding.rvAlarms.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                requireContext(), R.anim.layout_animation_from_bottom
            )
            alarmAdapter.submitList(alarmManagerHelper.getAlarms())
        }
    }

    private fun cancelScheduledAlarm(id: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)

        // Create a PendingIntent with the same request code used when setting the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            id, // Ensure this matches the request code used when setting the alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel() // Ensure the PendingIntent is also canceled
    }
}
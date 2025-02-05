package com.example.tiaalert.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiaalert.R
import com.example.tiaalert.databinding.ItemAlarmBinding
import com.example.tiaalert.main.model.AlarmsModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmAdapter(private val onDelete: (Int) -> Unit) :
    ListAdapter<AlarmsModel, AlarmAdapter.AlarmsViewHolder>(AlarmDiffCallback()) {

    inner class AlarmsViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(alarm: AlarmsModel) {
            with(binding) {
                tvName.text = alarm.name
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarm.hour)
                    set(Calendar.MINUTE, alarm.minute)
                }

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = timeFormat.format(calendar.time)

                tvTime.text = itemView.context.getString(R.string.time) + formattedTime
                tvRepeated.text = itemView.context.getString(
                    R.string.repeating_every_hours,
                    alarm.intervalHours.toString()
                )
                btnDelete.setOnClickListener {
                    val current = currentList.toMutableList()
                    current.removeAt(adapterPosition)
                    submitList(current)
                    onDelete(alarm.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmsViewHolder {
        return AlarmsViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<AlarmsModel>() {
        override fun areItemsTheSame(oldItem: AlarmsModel, newItem: AlarmsModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlarmsModel, newItem: AlarmsModel): Boolean {
            return oldItem == newItem
        }
    }
}





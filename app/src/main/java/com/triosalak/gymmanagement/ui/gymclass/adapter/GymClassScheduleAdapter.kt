package com.triosalak.gymmanagement.ui.gymclass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.triosalak.gymmanagement.data.model.entity.GymClassSchedule
import com.triosalak.gymmanagement.databinding.ItemGymClassScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class GymClassScheduleAdapter(
    private val onBookScheduleClick: (GymClassSchedule) -> Unit
) : ListAdapter<GymClassSchedule, GymClassScheduleAdapter.ScheduleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemGymClassScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding, onBookScheduleClick)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.bind(schedule)
    }

    class ScheduleViewHolder(
        private val binding: ItemGymClassScheduleBinding,
        private val onBookScheduleClick: (GymClassSchedule) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: GymClassSchedule) {
            binding.apply {
                // Format date
                schedule.date?.let { dateString ->
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                        val date = inputFormat.parse(dateString)
                        tvScheduleDate.text = date?.let { outputFormat.format(it) } ?: dateString
                    } catch (e: Exception) {
                        tvScheduleDate.text = dateString
                    }
                }

                // Format time
                val timeText = "${schedule.startTime} - ${schedule.endTime}"
                tvScheduleTime.text = timeText

                // Show available slots
                val slotsText = "${schedule.availableSlot}/${schedule.slot} slot tersedia"
                tvAvailableSlots.text = slotsText

                // Set slot availability indicator
                val isAvailable = (schedule.availableSlot ?: 0) > 0
                if (isAvailable) {
                    tvSlotStatus.text = "Tersedia"
                    tvSlotStatus.setBackgroundResource(com.triosalak.gymmanagement.R.drawable.background_status_available)
                    
                    // Enable booking button
                    btnBookSchedule.isEnabled = true
                    btnBookSchedule.text = "Pesan"
                    btnBookSchedule.alpha = 1.0f
                } else {
                    tvSlotStatus.text = "Penuh"
                    tvSlotStatus.setBackgroundResource(com.triosalak.gymmanagement.R.drawable.background_status_full)
                    
                    // Disable booking button
                    btnBookSchedule.isEnabled = false
                    btnBookSchedule.text = "Penuh"
                    btnBookSchedule.alpha = 0.5f
                }

                // Set click listener for booking button
                btnBookSchedule.setOnClickListener {
                    if (isAvailable) {
                        onBookScheduleClick(schedule)
                    }
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GymClassSchedule>() {
        override fun areItemsTheSame(oldItem: GymClassSchedule, newItem: GymClassSchedule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GymClassSchedule, newItem: GymClassSchedule): Boolean {
            return oldItem == newItem
        }
    }
}
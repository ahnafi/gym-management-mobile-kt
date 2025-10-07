package com.triosalak.gymmanagement.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triosalak.gymmanagement.data.model.entity.RecentVisit
import com.triosalak.gymmanagement.databinding.ItemRecentVisitBinding
import java.text.SimpleDateFormat
import java.util.*

class RecentVisitsAdapter : RecyclerView.Adapter<RecentVisitsAdapter.RecentVisitViewHolder>() {

    private var recentVisits: List<RecentVisit> = emptyList()

    fun updateData(newVisits: List<RecentVisit>) {
        recentVisits = newVisits
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentVisitViewHolder {
        val binding = ItemRecentVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentVisitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentVisitViewHolder, position: Int) {
        holder.bind(recentVisits[position])
    }

    override fun getItemCount(): Int = recentVisits.size

    class RecentVisitViewHolder(private val binding: ItemRecentVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(visit: RecentVisit) {
            // Format date (assuming visitDate is in format like "2025-10-04")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))

            try {
                val date = dateFormat.parse(visit.visitDate)
                binding.tvVisitDate.text = displayFormat.format(date ?: Date())
            } catch (_: Exception) {
                binding.tvVisitDate.text = visit.visitDate
            }

            // Format time display
            val timeText = if (visit.exitTime != null) {
                "Masuk: ${visit.entryTime} • Keluar: ${visit.exitTime}"
            } else {
                "Masuk: ${visit.entryTime} • Sedang di gym"
            }
            binding.tvVisitTime.text = timeText

            // Set status
            binding.tvVisitStatus.text = when (visit.status.lowercase()) {
                "completed" -> "Selesai"
                "active" -> "Aktif"
                else -> visit.status
            }
        }
    }
}

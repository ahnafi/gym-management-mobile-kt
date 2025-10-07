package com.triosalak.gymmanagement.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.triosalak.gymmanagement.data.model.entity.MyMembership
import com.triosalak.gymmanagement.databinding.ItemMembershipHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class MembershipHistoryAdapter : RecyclerView.Adapter<MembershipHistoryAdapter.MembershipViewHolder>() {

    private var memberships: List<MyMembership> = emptyList()

    fun updateData(newMemberships: List<MyMembership>) {
        memberships = newMemberships
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipViewHolder {
        val binding = ItemMembershipHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MembershipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MembershipViewHolder, position: Int) {
        holder.bind(memberships[position])
    }

    override fun getItemCount(): Int = memberships.size

    class MembershipViewHolder(private val binding: ItemMembershipHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(membership: MyMembership) {
            // Set membership name
            binding.tvMembershipName.text = membership.membershipPackage.name ?: "Unknown Package"

            // Format date period
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val displayFormat = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))

            try {
                val startDate = dateFormat.parse(membership.startDate)
                val endDate = dateFormat.parse(membership.endDate)

                val startDateStr = displayFormat.format(startDate ?: Date())
                val endDateStr = displayFormat.format(endDate ?: Date())

                binding.tvMembershipPeriod.text = "$startDateStr - $endDateStr"
            } catch (_: Exception) {
                binding.tvMembershipPeriod.text = "${membership.startDate} - ${membership.endDate}"
            }

            // Set status with Indonesian translation
            binding.tvMembershipStatus.text = when (membership.status.lowercase()) {
                "active" -> "Aktif"
                "expired" -> "Kedaluwarsa"
                "pending" -> "Menunggu"
                "cancelled" -> "Dibatalkan"
                else -> membership.status
            }
        }
    }
}

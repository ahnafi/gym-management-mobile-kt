package com.triosalak.gymmanagement.ui.membershippackages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.MembershipPackage
import com.triosalak.gymmanagement.databinding.ItemMembershipPackageBinding
import java.text.NumberFormat
import java.util.*

class MembershipPackageAdapter(
    private val onPackageClick: (MembershipPackage) -> Unit
) : ListAdapter<MembershipPackage, MembershipPackageAdapter.MembershipPackageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipPackageViewHolder {
        val binding = ItemMembershipPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MembershipPackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MembershipPackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MembershipPackageViewHolder(
        private val binding: ItemMembershipPackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(membershipPackage: MembershipPackage) {
            binding.apply {
                // Load package image using Coil
                ivPackageImage.load(membershipPackage.firstImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.exercise_24dp)
                    error(R.drawable.exercise_24dp)
                    transformations(RoundedCornersTransformation(16f))
                }
                
                // Package name
                tvPackageName.text = membershipPackage.name ?: "Unknown Package"
                
                // Duration handling
                val duration = membershipPackage.duration ?: 0
                tvPackageDuration.text = when {
                    duration == 0 -> "Sekali Bayar"
                    duration >= 365 -> "${duration / 365} Tahun"
                    duration >= 30 -> "${duration / 30} Bulan"
                    else -> "$duration Hari"
                }
                
                // Format price with Indonesian currency format
                val price = membershipPackage.price ?: 0
                val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
                tvPackagePrice.text = formatter.format(price)
                
                // Click listener for view detail button
                btnViewDetail.setOnClickListener {
                    onPackageClick(membershipPackage)
                }
                
                // Click listener for entire card
                root.setOnClickListener {
                    onPackageClick(membershipPackage)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<MembershipPackage>() {
        override fun areItemsTheSame(oldItem: MembershipPackage, newItem: MembershipPackage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MembershipPackage, newItem: MembershipPackage): Boolean {
            return oldItem == newItem
        }
    }
}
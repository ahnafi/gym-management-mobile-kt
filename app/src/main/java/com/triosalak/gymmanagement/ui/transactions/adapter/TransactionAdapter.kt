package com.triosalak.gymmanagement.ui.transactions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.triosalak.gymmanagement.data.model.entity.PurchasableGymClass
import com.triosalak.gymmanagement.data.model.entity.PurchasableMembershipPackage
import com.triosalak.gymmanagement.data.model.entity.Transaction
import com.triosalak.gymmanagement.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            with(binding) {
                // Set transaction code
                tvTransactionCode.text = transaction.code ?: "N/A"

                // Set payment status with localization and styling
                setPaymentStatus(transaction.paymentStatus)

                // Set purchasable type and item name
                setPurchasableInfo(transaction)

                // Set amount with proper formatting
                setAmount(transaction.amount)

                // Set created date with proper formatting
                setCreatedDate(transaction.createdAt)

                // Set click listener
                root.setOnClickListener { onItemClick(transaction) }
                btnViewDetail.setOnClickListener { onItemClick(transaction) }
            }
        }

        private fun setPaymentStatus(status: String?) {
            val (statusText, backgroundColor, textColor) = when (status?.lowercase()) {
                "pending" -> Triple("Menunggu", android.R.color.holo_orange_light, android.R.color.holo_orange_dark)
                "paid" -> Triple("Berhasil", android.R.color.holo_green_light, android.R.color.holo_green_dark)
                "failed" -> Triple("Gagal", android.R.color.holo_red_light, android.R.color.holo_red_dark)
                else -> Triple("Unknown", android.R.color.darker_gray, android.R.color.black)
            }

            binding.tvPaymentStatus.text = statusText
            binding.tvPaymentStatus.setBackgroundResource(backgroundColor)
            binding.tvPaymentStatus.setTextColor(binding.root.context.getColor(textColor))
        }

        private fun setPurchasableInfo(transaction: Transaction) {
            val purchasable = transaction.purchasable
            val purchasableType = transaction.purchasableType

            // Set purchasable type with localization
            val typeText = when (purchasableType) {
                "membership_package" -> "Paket Membership"
                "gym_class" -> "Kelas Gym"
                else -> purchasableType ?: "Unknown"
            }
            binding.tvPurchasableType.text = typeText

            // Set item name based on purchasable type
            val itemName = when (purchasable) {
                is PurchasableMembershipPackage -> purchasable.name ?: "Paket Membership"
                is PurchasableGymClass -> purchasable.name ?: "Kelas Gym"
                else -> "Item tidak diketahui"
            }
            binding.tvItemName.text = itemName
        }

        private fun setAmount(amount: Int?) {
            if (amount != null) {
                val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
                binding.tvAmount.text = formatter.format(amount)
            } else {
                binding.tvAmount.text = "0"
            }
        }

        private fun setCreatedDate(createdAt: String?) {
            if (createdAt != null) {
                try {
                    // Parse ISO 8601 date format (2025-10-05T14:47:14.000000Z)
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                    
                    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                    
                    val date = inputFormat.parse(createdAt)
                    binding.tvCreatedAt.text = date?.let { outputFormat.format(it) } ?: createdAt
                } catch (e: Exception) {
                    // Fallback to simple date parsing
                    try {
                        val fallbackFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                        
                        val date = fallbackFormat.parse(createdAt.substring(0, 10))
                        binding.tvCreatedAt.text = date?.let { outputFormat.format(it) } ?: createdAt
                    } catch (e2: Exception) {
                        binding.tvCreatedAt.text = createdAt
                    }
                }
            } else {
                binding.tvCreatedAt.text = "N/A"
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

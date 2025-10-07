package com.triosalak.gymmanagement.ui.gymclass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.databinding.ItemGymClassBinding
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.utils.toRupiahInstant

class ClassAdapter(
    private var classList: List<GymClass>,
    private val onItemClick: ((GymClass) -> Unit)
) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGymClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GymClass) {
            binding.tvGymName.text = item.name

            binding.tvGymPrice.text = item.price?.toRupiahInstant() ?: "-"
            binding.tvGymDescription.text = item.description ?: "-"

            // Jika ada gambar, tampilkan gambar pertama pakai Coil
            val imageUrl = item.images?.firstOrNull()
            binding.imgGymClass.load(Constants.STORAGE_URL + imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.placeholder_image)
            }

            // Klik item
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    fun updateData(newItems: List<GymClass>) {
        classList = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGymClassBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(classList[position])
    }

    override fun getItemCount(): Int = classList.size
}

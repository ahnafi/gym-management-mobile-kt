package com.triosalak.gymmanagement.ui.gymclass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.databinding.ItemGymClassBinding

class ClassAdapter(
    private val classList: List<GymClass>,
    private val onItemClick: ((GymClass) -> Unit)? = null
) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGymClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GymClass) {
            binding.tvGymName.text = item.name
            binding.tvGymPrice.text = item.price.toString()
            binding.tvGymDescription.text = item.description

            // Load image (pakai Glide, pastikan sudah ada dependensinya)
//            Glide.with(binding.root.context)
//                .load(item.imageUrl)
//                .placeholder(R.drawable.placeholder_image)
//                .into(binding.imgGymClass)

            // Klik item
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
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

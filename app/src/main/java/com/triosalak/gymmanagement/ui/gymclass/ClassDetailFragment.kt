package com.triosalak.gymmanagement.ui.gymclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentClassDetailBinding
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.viewmodel.ClassViewModel
import java.util.Locale

class ClassDetailFragment : Fragment() {

    private var _binding: FragmentClassDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var classViewModel: ClassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = com.triosalak.gymmanagement.utils.SessionManager(requireContext())
        classViewModel = ClassViewModel(RetrofitInstance.getApiService(sessionManager))

        // Ambil ID kelas dari arguments
        val classId = arguments?.getInt("classId")

        // Fetch data detail kelas berdasarkan ID
        if (classId != null) {
            classViewModel.getGymClassDetail(classId)
        }

        setupObservers()

        setBtnBack()
    }

    private fun setupObservers() {
        classViewModel.gymClassDetail.observe(viewLifecycleOwner) { gymClass ->
            gymClass?.let {
                // Display class name
                binding.tvClassName.text = it.name

                // Display class description
                binding.tvClassDescription.text = it.description ?: "Tidak ada deskripsi"

                // Display class price
                val priceText = if (it.price != null) {
                    "Rp ${String.format(Locale("id", "ID"), "%,d", it.price)}"
                } else {
                    "Harga belum tersedia"
                }
                binding.tvClassPrice.text = priceText

                // Display class image
                val imageUrl = it.images?.firstOrNull()
                binding.ivClassImage.load(Constants.STORAGE_URL + imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.placeholder_image)
                }

            }
        }

        // Observer untuk loading state
//        classViewModel.isLoadingDetail.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }
    }

    private fun setBtnBack() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_classDetailFragment_to_navigation_kelas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}
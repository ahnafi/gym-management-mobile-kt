package com.triosalak.gymmanagement.ui.gymclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.data.model.entity.GymClassSchedule
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentClassDetailBinding
import com.triosalak.gymmanagement.ui.gymclass.adapter.GymClassScheduleAdapter
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.viewmodel.ClassDetailViewModel
import java.util.Locale

class ClassDetailFragment : Fragment() {

    private var _binding: FragmentClassDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var classDetailViewModel: ClassDetailViewModel
    private lateinit var schedulesAdapter: GymClassScheduleAdapter
    private var currentGymClassId: Int? = null

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
        classDetailViewModel = ClassDetailViewModel(RetrofitInstance.getApiService(sessionManager))

        setupRecyclerView()
        
        // Ambil ID kelas dari arguments
        val classId = arguments?.getInt("classId")
        currentGymClassId = classId

        // Fetch data detail kelas dan jadwal berdasarkan ID
        if (classId != null) {
            classDetailViewModel.getGymClassDetail(classId)
            classDetailViewModel.getGymClassSchedules(classId)
        }

        setupObservers()
        setBtnBack()
    }

    private fun setupRecyclerView() {
        schedulesAdapter = GymClassScheduleAdapter { schedule ->
            onBookScheduleClicked(schedule)
        }
        binding.rvSchedules.apply {
            adapter = schedulesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onBookScheduleClicked(schedule: GymClassSchedule) {
        val gymClassId = currentGymClassId
        val scheduleId = schedule.id
        
        if (gymClassId != null && scheduleId != null) {
            // Check if slots are available
            val availableSlots = schedule.availableSlot ?: 0
            if (availableSlots > 0) {
                // Initiate payment
                classDetailViewModel.initiateGymClassPayment(gymClassId, scheduleId)
            } else {
                Toast.makeText(requireContext(), "Maaf, slot untuk jadwal ini sudah penuh", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Terjadi kesalahan saat memproses pemesanan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        classDetailViewModel.gymClass.observe(viewLifecycleOwner) { gymClass ->
            gymClass?.let {
                // Display class name
                binding.tvClassName.text = it.name

                // Display class description
                binding.tvClassDescription.text = it.description ?: "Tidak ada deskripsi"

                // Display class price
                val priceText = if (it.price != null) {
                    "Rp ${String.format(Locale.forLanguageTag("id-ID"), "%,d", it.price)}"
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

        classDetailViewModel.gymClassSchedules.observe(viewLifecycleOwner) { schedules ->
            if (schedules.isNotEmpty()) {
                binding.rvSchedules.visibility = View.VISIBLE
                binding.layoutEmptySchedules.visibility = View.GONE
                schedulesAdapter.submitList(schedules)
            } else {
                binding.rvSchedules.visibility = View.GONE
                binding.layoutEmptySchedules.visibility = View.VISIBLE
            }
        }

        // Observer untuk loading state class detail
        classDetailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer untuk loading state schedules
        classDetailViewModel.isLoadingSchedules.observe(viewLifecycleOwner) { isLoading ->
            binding.progressSchedules.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer untuk payment initiation loading
        classDetailViewModel.isInitiatingPayment.observe(viewLifecycleOwner) { isLoading ->
            // You can show a loading dialog or disable buttons here
            if (isLoading) {
                Toast.makeText(requireContext(), "Memproses pembayaran...", Toast.LENGTH_SHORT).show()
            }
        }

        // Observer untuk payment initiation result
        classDetailViewModel.paymentInitiated.observe(viewLifecycleOwner) { paymentResponse ->
            paymentResponse?.let {
                if (it.status == "success") {
                    Toast.makeText(
                        requireContext(), 
                        "Pembayaran berhasil diinisiasi! ${it.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // You can navigate to payment screen or show payment details here
                    // For now, just refresh the schedules to update slot availability
                    currentGymClassId?.let { classId ->
                        classDetailViewModel.getGymClassSchedules(classId)
                    }
                } else {
                    Toast.makeText(
                        requireContext(), 
                        "Gagal memproses pembayaran: ${it.message}", 
                        Toast.LENGTH_LONG
                    ).show()
                }
                classDetailViewModel.clearPaymentInitiated()
            }
        }

        // Observer untuk error message
        classDetailViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                classDetailViewModel.clearErrorMessage()
            }
        }
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
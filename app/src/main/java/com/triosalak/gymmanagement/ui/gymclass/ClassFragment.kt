package com.triosalak.gymmanagement.ui.gymclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.triosalak.gymmanagement.data.model.entity.GymClass
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import com.triosalak.gymmanagement.databinding.FragmentClassBinding
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.ClassViewModel

class ClassFragment : Fragment() {

    private var _binding: FragmentClassBinding? = null
    private val binding get() = _binding!!

    private lateinit var classViewModel: ClassViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        classViewModel = ClassViewModel(RetrofitInstance.getApiService(sessionManager))

        setupRecyclerView()
        loadClasses()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewKelas.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun loadClasses() {
        // Contoh data dummy – nanti bisa diganti dari API
        val dummyList = listOf(
            GymClass(1, "Kelas menari membakar kalori", "Rp 150.000", "https://example.com/zumba.jpg"),
            GymClass(2, "Fokus dan keseimbangan tubuh", "Rp 120.000", "https://example.com/yoga.jpg"),
            GymClass(3, "Latihan intensitas tinggi", "Rp 200.000", "https://example.com/hiit.jpg"),
            GymClass(4, "Gerakan ringan untuk fleksibilitas", "Rp 130.000", "https://example.com/pilates.jpg")
        )

        val adapter = ClassAdapter(dummyList) { gymClass ->
            Toast.makeText(requireContext(), "Anda memilih: ${gymClass.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewKelas.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

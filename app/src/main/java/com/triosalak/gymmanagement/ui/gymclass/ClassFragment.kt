package com.triosalak.gymmanagement.ui.gymclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.triosalak.gymmanagement.R
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

    private lateinit var adapter: ClassAdapter

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
        setupObservers()
        setupLoadMore()

        // Load halaman pertama
        classViewModel.getGymClass()
    }

    private fun setupLoadMore() {
        binding.btnLoadMore.setOnClickListener {
            val nextPage = classViewModel.getNextPage()
            classViewModel.getGymClass(page = nextPage, append = true)
        }
    }

    private fun setupRecyclerView() {
        adapter = ClassAdapter(emptyList()) { gymClass ->
            navigateToDetail(gymClass)
        }

        binding.recyclerViewKelas.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewKelas.adapter = adapter
    }

    fun navigateToDetail(gymClass: GymClass) {
        // Navigasi ke ClassDetailFragment dengan mengoper ID kelas
        val classId = gymClass.id ?: return // Return jika ID null

        val bundle = Bundle().apply {
            putInt("classId", classId)
        }
        findNavController().navigate(
            R.id.action_navigation_kelas_to_classDetailFragment,
            bundle
        )
    }

    private fun setupObservers() {
        classViewModel.gymClasses.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)

            // jika halaman terakhir, sembunyikan tombol
            binding.btnLoadMore.visibility =
                if (classViewModel.canLoadMore()) View.VISIBLE else View.GONE
        }

        // Observer untuk loading state
        classViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

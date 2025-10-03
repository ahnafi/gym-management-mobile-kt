package com.triosalak.gymmanagement.ui.profile

import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.triosalak.gymmanagement.R
import com.triosalak.gymmanagement.databinding.DialogEditProfileBinding
import com.triosalak.gymmanagement.databinding.FragmentProfileBinding
import com.triosalak.gymmanagement.utils.Constants
import com.triosalak.gymmanagement.utils.SessionManager
import com.triosalak.gymmanagement.viewmodel.ProfileViewModel
import com.triosalak.gymmanagement.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    private var editProfileDialog: Dialog? = null
    private var editProfileBinding: DialogEditProfileBinding? = null
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.d(TAG, "Image picker result received: $uri")
        uri?.let {
            Log.i(TAG, "✅ Image selected successfully: $it")
            selectedImageUri = it
            editProfileBinding?.ivProfilePicture?.load(it)
            Log.d(TAG, "Image loaded into dialog preview")
        } ?: run {
            Log.w(TAG, "⚠️ No image selected by user")
        }
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permission result received: isGranted=$isGranted")
        if (isGranted) {
            Log.i(TAG, "✅ Permission granted, opening image picker")
            openImagePicker()
        } else {
            Log.w(TAG, "❌ Permission denied by user")
            Toast.makeText(requireContext(), "Permission diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "=== ProfileFragment onCreateView ===")
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        Log.d(TAG, "Fragment binding created")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "=== ProfileFragment onViewCreated ===")

        Log.d(TAG, "Initializing SessionManager...")
        sessionManager = SessionManager(requireContext())

        Log.d(TAG, "Initializing ViewModel with RetrofitInstance...")
        // Initialize ViewModel using RetrofitInstance
        val api = RetrofitInstance.getApiService(sessionManager)
        profileViewModel = ProfileViewModel(api, sessionManager)
        Log.i(TAG, "ProfileViewModel initialized successfully")

        // Setup button click listeners
        setupButtonClickListeners()

        // Observe user data
        observeUserData()

        // Observe ViewModel states
        observeViewModel()

        // Load user data
        loadUserDataSync()

        Log.i(TAG, "✅ ProfileFragment setup completed")
    }

    private fun setupButtonClickListeners() {
        Log.d(TAG, "Setting up button click listeners...")

        // Logout button
        binding.btnLogout.setOnClickListener {
            Log.i(TAG, "🔴 Logout button clicked")
            performLogout()
        }

        // Edit profile button
        binding.btnEditProfile.setOnClickListener {
            Log.i(TAG, "✏️ Edit Profile button clicked")
            showEditProfileDialog()
        }

        Log.d(TAG, "Button click listeners setup completed")
    }

    private fun showEditProfileDialog() {
        Log.d(TAG, "=== Showing Edit Profile Dialog ===")

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_profile, null)
            Log.d(TAG, "Dialog view inflated successfully")

            editProfileBinding = DialogEditProfileBinding.bind(dialogView)
            Log.d(TAG, "Dialog binding created")

            // Load current user data into dialog
            val currentUser = sessionManager.getCurrentUserSync()
            Log.d(TAG, "Current user data: ${currentUser?.let { "ID:${it.id}, Name:${it.name}, Phone:${it.phone}, Bio:${it.profileBio}" } ?: "null"}")

            currentUser?.let { user ->
                editProfileBinding?.apply {
                    Log.d(TAG, "Populating dialog fields...")
                    etName.setText(user.name)
                    etPhone.setText(user.phone)
                    etBio.setText(user.profileBio)
                    Log.d(TAG, "Text fields populated - Name:'${user.name}', Phone:'${user.phone}', Bio length:${user.profileBio?.length ?: 0}")

                    // Load profile image with error handling
                    try {
                        val imageUrl = "${Constants.STORAGE_URL}${user.profileImage}"
                        Log.d(TAG, "Loading profile image from: $imageUrl")
                        ivProfilePicture.load(imageUrl) {
                            crossfade(true)
                            placeholder(R.drawable.ic_camera)
                            error(R.drawable.ic_camera)
                        }
                        Log.d(TAG, "Profile image load initiated")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading profile image: ${e.message}", e)
                    }
                }
            } ?: run {
                Log.w(TAG, "⚠️ No current user data found for dialog")
            }

            // Setup dialog
            editProfileDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
            Log.d(TAG, "MaterialAlertDialog created")

            // Setup dialog button listeners
            setupEditDialogListeners()

            editProfileDialog?.show()
            Log.i(TAG, "✅ Edit Profile Dialog displayed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error showing edit profile dialog: ${e.message}", e)
            Toast.makeText(requireContext(), "Error membuka dialog edit profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupEditDialogListeners() {
        Log.d(TAG, "Setting up edit dialog button listeners...")

        editProfileBinding?.apply {
            // Change photo button
            fabChangePhoto.setOnClickListener {
                Log.i(TAG, "📷 Change photo button clicked")
                checkPermissionAndOpenImagePicker()
            }

            // Cancel button
            btnCancel.setOnClickListener {
                Log.i(TAG, "❌ Cancel button clicked")
                dismissEditDialog()
            }

            // Save button
            btnSave.setOnClickListener {
                Log.i(TAG, "💾 Save button clicked")
                saveProfile()
            }
        }

        Log.d(TAG, "Dialog button listeners setup completed")
    }

    private fun checkPermissionAndOpenImagePicker() {
        Log.d(TAG, "=== Checking Permissions for Image Picker ===")

        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        Log.d(TAG, "Required permission: $permission (Android SDK: ${android.os.Build.VERSION.SDK_INT})")

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(TAG, "✅ Permission already granted, opening image picker")
                openImagePicker()
            }
            else -> {
                Log.i(TAG, "🔒 Permission not granted, requesting permission")
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        Log.d(TAG, "=== Opening Image Picker ===")

        try {
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            imagePickerLauncher.launch(request)
            Log.i(TAG, "✅ Image picker launched successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error launching image picker: ${e.message}", e)
            Toast.makeText(requireContext(), "Error membuka galeri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        Log.d(TAG, "=== Saving Profile Changes ===")

        editProfileBinding?.let { binding ->
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val bio = binding.etBio.text.toString().trim()

            Log.d(TAG, "Form data - Name:'$name', Phone:'$phone', Bio:'$bio'")
            Log.d(TAG, "Selected image URI: $selectedImageUri")

            var hasTextChanges = false
            var hasImageChange = false

            // Update profile text data first
            if (name.isNotBlank() || phone.isNotBlank() || bio.isNotBlank()) {
                Log.i(TAG, "📝 Updating profile text data...")
                hasTextChanges = true
                profileViewModel.updateProfile(
                    name = name.ifBlank { null },
                    phone = phone.ifBlank { null },
                    bio = bio.ifBlank { null }
                )
            }

            // Update profile photo if selected
            selectedImageUri?.let { uri ->
                hasImageChange = true
                profileViewModel.updatePhotoProfile(requireContext(), uri)
                Log.i(TAG, "📷 Updating profile photo...")
            }

            // If no changes made
            if (!hasTextChanges && !hasImageChange) {
                Log.w(TAG, "⚠️ No changes detected, dismissing dialog")
                dismissEditDialog()
                return
            }

            Log.i(TAG, "✅ Save operations initiated - Text:$hasTextChanges, Image:$hasImageChange")
        } ?: run {
            Log.e(TAG, "❌ Edit dialog binding is null, cannot save")
        }
    }

    private fun observeViewModel() {
        Log.d(TAG, "Setting up ViewModel observers...")

        // Observe loading state
        lifecycleScope.launch {
            profileViewModel.isLoading.collect { isLoading ->
                Log.d(TAG, "Loading state changed: $isLoading")
                editProfileBinding?.apply {
                    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    btnSave.isEnabled = !isLoading
                    btnCancel.isEnabled = !isLoading
                }
            }
        }

        // Observe update result
        lifecycleScope.launch {
            profileViewModel.updateResult.collect { result ->
                result?.let {
                    Log.d(TAG, "Update result received: ${it::class.simpleName}")
                    when (it) {
                        is ProfileViewModel.UpdateResult.Success -> {
                            Log.i(TAG, "✅ Profile update SUCCESS: ${it.message}")
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            dismissEditDialog()

                            // Force refresh user data display
                            refreshUserDisplay()
                        }
                        is ProfileViewModel.UpdateResult.Error -> {
                            Log.e(TAG, "❌ Profile update ERROR: ${it.message}")
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    profileViewModel.clearResult()
                }
            }
        }

        Log.d(TAG, "ViewModel observers setup completed")
    }

    private fun refreshUserDisplay() {
        Log.d(TAG, "=== Refreshing User Display ===")

        // Force reload user data to ensure UI is updated
        val currentUser = sessionManager.getCurrentUserSync()
        currentUser?.let { user ->
            Log.i(TAG, "Refreshing UI with updated user data")
            Log.d(TAG, "Updated data: ID:${user.id}, Name:${user.name}, Email:${user.email}, Phone:${user.phone}")

            binding.apply {
                tvUserName.text = user.name ?: "Nama tidak tersedia"
                tvUserEmail.text = user.email
                tvUserPhone.text = user.phone ?: "Phone tidak tersedia"
                tvUserRole.text = user.role ?: "Role tidak tersedia"
                tvMembershipStatus.text = user.membershipStatus ?: "Tidak aktif"
                tvMembershipEndDate.text = user.membershipEndDate ?: "Tidak ada"
                tvProfileBio.text = user.profileBio ?: "Bio kosong"

                // Load profile image with better error handling
                val imageUrl = "${Constants.STORAGE_URL}${user.profileImage}"
                Log.d(TAG, "Loading updated profile image from: $imageUrl")

                ivProfilePicture.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_camera)
                    error(R.drawable.ic_camera)
                }
            }
            Log.i(TAG, "✅ UI refresh completed successfully")
        } ?: run {
            Log.e(TAG, "❌ No user data found after update!")
        }
    }

    private fun dismissEditDialog() {
        Log.d(TAG, "=== Dismissing Edit Dialog ===")

        selectedImageUri = null
        editProfileBinding = null
        editProfileDialog?.dismiss()
        editProfileDialog = null

        Log.i(TAG, "✅ Edit dialog dismissed and cleaned up")
    }

    private fun performLogout() {
        Log.d(TAG, "=== Performing Logout ===")

        lifecycleScope.launch {
            try {
                Log.i(TAG, "Clearing all user data...")
                // Clear all user data
                sessionManager.clearAllData()

                Log.i(TAG, "Navigating to AuthActivity...")
                // Navigate back to AuthActivity
                val intent = android.content.Intent(requireContext(), com.triosalak.gymmanagement.AuthActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()

                Log.i(TAG, "✅ Logout completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during logout: ${e.message}", e)
                Toast.makeText(requireContext(), "Error saat logout: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeUserData() {
        Log.d(TAG, "Setting up user data observer...")

        // Observe user data secara realtime
        lifecycleScope.launch {
            sessionManager.currentUser.collect { user ->
                user?.let {
                    Log.d(TAG, "👤 User data updated from SessionManager")
                    // Update UI dengan data user
                    binding.apply {
                        tvUserName.text = it.name ?: "Nama tidak tersedia"
                        tvUserEmail.text = it.email
                        tvUserPhone.text = it.phone ?: "Phone tidak tersedia"
                        tvUserRole.text = it.role ?: "Role tidak tersedia"
                        tvMembershipStatus.text = it.membershipStatus ?: "Tidak aktif"
                        tvMembershipEndDate.text = it.membershipEndDate ?: "Tidak ada"
                        tvProfileBio.text = it.profileBio ?: "Bio kosong"
                        ivProfilePicture.load("${Constants.STORAGE_URL}${it.profileImage}")

                        // Log semua data user
                        Log.d(TAG, "USER_DATA - ID: ${it.id}")
                        Log.d(TAG, "USER_DATA - Name: ${it.name}")
                        Log.d(TAG, "USER_DATA - Email: ${it.email}")
                        Log.d(TAG, "USER_DATA - Phone: ${it.phone}")
                        Log.d(TAG, "USER_DATA - Role: ${it.role}")
                        Log.d(TAG, "USER_DATA - Membership: ${it.membershipStatus}")
                        Log.d(TAG, "USER_DATA - Created: ${it.createdAt}")
                        Log.d(TAG, "USER_DATA - Profile image: ${Constants.STORAGE_URL}${it.profileImage}")
                        Log.d(TAG, "USER_DATA - Email verified: ${it.emailVerifiedAt}")
                    }
                } ?: run {
                    Log.w(TAG, "⚠️ User data is null")
                }
            }
        }

        Log.d(TAG, "User data observer setup completed")
    }

    private fun loadUserDataSync() {
        Log.d(TAG, "=== Loading User Data Sync ===")

        // Cara mengambil user data secara synchronous
        val currentUser = sessionManager.getCurrentUserSync()
        currentUser?.let { user ->
            Log.i(TAG, "✅ User loaded synchronously: ${user.name} - ${user.email}")
            Log.d(TAG, "User email verification: ${user.emailVerifiedAt}")
        } ?: run {
            Log.w(TAG, "⚠️ No user data found in sync load")
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "=== ProfileFragment onDestroyView ===")
        super.onDestroyView()
        dismissEditDialog()
        _binding = null
        Log.i(TAG, "✅ ProfileFragment cleaned up")
    }
}

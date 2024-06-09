package com.l0122138.ridlo.sharetaskapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.l0122138.ridlo.sharetaskapp.MainActivity
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val maxInputLength = 50
    private val maxImageSize = 5 * 1024 * 1024 // 5 MB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("profile", Context.MODE_PRIVATE)

        profileViewModel.name.observe(viewLifecycleOwner) { name ->
            binding.nameEditText.setText(name)
        }

        profileViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.emailEditText.setText(email)
        }

        profileViewModel.phone.observe(viewLifecycleOwner) { phone ->
            binding.phoneEditText.setText(phone)
        }

        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.profileImage.setImageURI(it)
            }
        }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri: Uri? = result.data?.data
                    imageUri?.let {
                        if (validateImageSize(it)) {
                            binding.profileImage.setImageURI(it)
                            saveImageToInternalStorage(it)
                            profileViewModel.saveProfileData(
                                binding.nameEditText.text.toString(),
                                binding.emailEditText.text.toString(),
                                binding.phoneEditText.text.toString(),
                                it
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Image size is too large",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        binding.changePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()

            if (validateInputs(name, email, phone)) {
                profileViewModel.saveProfileData(
                    name,
                    email,
                    phone,
                    profileViewModel.profileImageUri.value
                )
                Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
            }
        }

//        theme button
        binding.themeButton.setOnClickListener {
            showThemeDialog()
        }

        applyTheme(getSavedTheme())
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as MainActivity).hideActionBar()
//    }

//    override fun onPause() {
//        super.onPause()
//        (activity as MainActivity).showActionBar()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateInputs(name: String, email: String, phone: String): Boolean {
        if (name.length > maxInputLength || email.length > maxInputLength || phone.length > maxInputLength) {
            Toast.makeText(requireContext(), "Input text is too long", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateImageSize(uri: Uri): Boolean {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val fileSize = inputStream?.available() ?: 0
        inputStream?.close()
        return fileSize <= maxImageSize
    }

    private fun saveImageToInternalStorage(imageUri: Uri) {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }
        val file = File(requireContext().filesDir, "profile_image.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("System Theme", "Light Theme", "Dark Theme")
        val checkedItem = getSavedTheme() // Get the saved theme

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose Theme")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                saveTheme(which)
                applyTheme(which)
                dialog.dismiss()
            }
            .show()
    }

    private fun getSavedTheme(): Int {
        return sharedPreferences.getInt("theme", 0) // Default to system theme
    }

    private fun saveTheme(theme: Int) {
        with(sharedPreferences.edit()) {
            putInt("theme", theme)
            apply()
        }
    }

    private fun applyTheme(theme: Int) {
        when (theme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
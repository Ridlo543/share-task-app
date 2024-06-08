package com.l0122138.ridlo.sharetaskapp.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File


class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("profile", Context.MODE_PRIVATE)

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> get() = _profileImageUri

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        _name.value = sharedPreferences.getString("name", "")
        _email.value = sharedPreferences.getString("email", "")
        _phone.value = sharedPreferences.getString("phone", "")
        val file = File(getApplication<Application>().filesDir, "profile_image.png")
        if (file.exists()) {
            _profileImageUri.value = Uri.fromFile(file)
        }
    }

    fun saveProfileData(name: String, email: String, phone: String, imageUri: Uri?) {
        with(sharedPreferences.edit()) {
            putString("name", name)
            putString("email", email)
            putString("phone", phone)
            apply()
        }
        _name.value = name
        _email.value = email
        _phone.value = phone
        imageUri?.let {
            _profileImageUri.value = it
        }
    }
}
package com.l0122138.ridlo.sharetaskapp.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.l0122138.ridlo.sharetaskapp.model.ClassCreateRequest
import com.l0122138.ridlo.sharetaskapp.model.ClassData
import com.l0122138.ridlo.sharetaskapp.model.ClassUpdateRequest
import com.l0122138.ridlo.sharetaskapp.util.ApiService
import com.l0122138.ridlo.sharetaskapp.util.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassViewModel(application: Application) : AndroidViewModel(application) {
    internal val _classes = MutableLiveData<List<ClassData>>()
    val classes: LiveData<List<ClassData>> get() = _classes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchClasses() {
        _loading.value = true
        val sharedPreferences = getApplication<Application>().getSharedPreferences("MyClasses", Context.MODE_PRIVATE)
        val classCodes = sharedPreferences.getStringSet("class_codes", mutableSetOf()) ?: mutableSetOf()

        if (classCodes.isNotEmpty()) {
            val fetchedClasses = mutableListOf<ClassData>()
            classCodes.forEach { code ->
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                apiService.getClassByCode(code).enqueue(object : Callback<ClassData> {
                    override fun onResponse(call: Call<ClassData>, response: Response<ClassData>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                fetchedClasses.add(it)
                                if (fetchedClasses.size == classCodes.size) {
                                    _classes.value = fetchedClasses
                                    _loading.value = false
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ClassData>, t: Throwable) {
                        _loading.value = false
                    }
                })
            }
        } else {
            _classes.value = emptyList()
            _loading.value = false
        }
    }

    fun createClass(name: String) {
        _loading.value = true
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.createClass(ClassCreateRequest(name)).enqueue(object : Callback<ClassData> {
            override fun onResponse(call: Call<ClassData>, response: Response<ClassData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val updatedClasses = _classes.value.orEmpty().toMutableList()
                        updatedClasses.add(it)
                        _classes.value = updatedClasses
                        _loading.value = false
                    }
                } else {
                    _loading.value = false
                }
            }

            override fun onFailure(call: Call<ClassData>, t: Throwable) {
                _loading.value = false
            }
        })
    }

    fun updateClass(code: String, name: String) {
        _loading.value = true
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.updateClass(code, ClassUpdateRequest(name)).enqueue(object : Callback<ClassData> {
            override fun onResponse(call: Call<ClassData>, response: Response<ClassData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        fetchClasses() // Fetch updated list
                        _loading.value = false
                    }
                } else {
                    _loading.value = false
                }
            }

            override fun onFailure(call: Call<ClassData>, t: Throwable) {
                _loading.value = false
            }
        })
    }

    fun deleteClass(code: String) {
        _loading.value = true
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.deleteClass(code).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val updatedClasses = _classes.value.orEmpty().toMutableList()
                    updatedClasses.removeAll { it.code == code }
                    _classes.value = updatedClasses
                    _loading.value = false
                } else {
                    _loading.value = false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _loading.value = false
            }
        })
    }
}

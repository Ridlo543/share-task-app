package com.l0122138.ridlo.sharetaskapp.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
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
    private val _classes = MutableLiveData<List<ClassData>>()
    val classes: LiveData<List<ClassData>> get() = _classes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        fetchClasses()
    }

    fun fetchClasses() {
        _loading.value = true
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("MyClasses", Context.MODE_PRIVATE)
        val classCodes =
            sharedPreferences.getStringSet("class_codes", mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()

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
                        } else {
                            if (fetchedClasses.size == classCodes.size) {
                                _loading.value = false
                            }
                        }
                    }

                    override fun onFailure(call: Call<ClassData>, t: Throwable) {
                        if (fetchedClasses.size == classCodes.size) {
                            _loading.value = false
                        }
                    }
                })
            }
        } else {
            _classes.value = emptyList()
            _loading.value = false
        }
    }

    fun fetchClassByCode(code: String) {
        _loading.value = true
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getClassByCode(code).enqueue(object : Callback<ClassData> {
            override fun onResponse(call: Call<ClassData>, response: Response<ClassData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val updatedClasses = _classes.value.orEmpty().toMutableList()
                        updatedClasses.add(it)
                        _classes.value = updatedClasses
                        saveClassCodeToSharedPreferences(code)
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

    private fun saveClassCodeToSharedPreferences(code: String) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("MyClasses", Context.MODE_PRIVATE)
        val classCodes =
            sharedPreferences.getStringSet("class_codes", mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()
        classCodes.add(code)
        sharedPreferences.edit().putStringSet("class_codes", classCodes).apply()
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
                        saveClassCodeToSharedPreferences(it.code)
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
        apiService.updateClass(code, ClassUpdateRequest(name))
            .enqueue(object : Callback<ClassData> {
                override fun onResponse(call: Call<ClassData>, response: Response<ClassData>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            fetchClasses()
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

    fun deleteClassFromList(code: String) {
        val updatedClasses = _classes.value.orEmpty().toMutableList()
        updatedClasses.removeAll { it.code == code }
        _classes.value = updatedClasses
        removeClassCodeFromSharedPreferences(code)
    }

    fun deleteClassFromDatabase(code: String) {
        _loading.value = true
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.deleteClass(code).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    deleteClassFromList(code) // Update list after successful deletion from database
                    _loading.value = false
                } else {
                    // Handle unsuccessful response
                    Log.e(
                        "ClassViewModel",
                        "Failed to delete class from database: ${response.errorBody()?.string()}"
                    )
                    _loading.value = false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ClassViewModel", "Error deleting class from database", t)
                _loading.value = false
            }
        })
    }

    private fun removeClassCodeFromSharedPreferences(code: String) {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("MyClasses", Context.MODE_PRIVATE)
        val classCodes =
            sharedPreferences.getStringSet("class_codes", mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()
        classCodes.remove(code)
        sharedPreferences.edit().putStringSet("class_codes", classCodes).apply()
    }
}

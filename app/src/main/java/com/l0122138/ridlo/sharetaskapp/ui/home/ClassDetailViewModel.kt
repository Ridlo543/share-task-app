package com.l0122138.ridlo.sharetaskapp.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import com.l0122138.ridlo.sharetaskapp.util.ApiService
import com.l0122138.ridlo.sharetaskapp.util.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _tasks = MutableLiveData<List<TaskData>>()
    val tasks: LiveData<List<TaskData>> get() = _tasks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    fun fetchTasks(classCode: String) {
        _loading.value = true
        apiService.getTasksByClassCode(classCode).enqueue(object : Callback<List<TaskData>> {
            override fun onResponse(call: Call<List<TaskData>>, response: Response<List<TaskData>>) {
                if (response.isSuccessful) {
                    _tasks.value = response.body()
                    saveTasksToSharedPreferences(classCode, response.body().orEmpty())
                } else {
                    _tasks.value = emptyList()
                }
                _loading.value = false
            }

            override fun onFailure(call: Call<List<TaskData>>, t: Throwable) {
                _tasks.value = emptyList()
                _loading.value = false
            }
        })
    }

    private fun saveTasksToSharedPreferences(classCode: String, tasks: List<TaskData>) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("MyTasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val jsonTasks = gson.toJson(tasks)
        editor.putString(classCode, jsonTasks)
        editor.apply()
    }

    fun addTask(classCode: String, name: String, description: String, deadline: String) {
        _loading.value = true
        val taskData = TaskData(name = name, description = description, deadline = deadline)

        viewModelScope.launch {
            try {
                val newTask = apiService.addTask(classCode, taskData)
                _tasks.value = _tasks.value.orEmpty() + newTask
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTask(taskId: String, classCode: String, name: String, description: String, deadline: String) {
        _loading.value = true
        val taskData = TaskData(id = taskId, name = name, description = description, deadline = deadline)

        viewModelScope.launch {
            try {
                val updatedTask = apiService.updateTask(classCode, taskId, taskData)
                _tasks.value = _tasks.value?.map { if (it.id == taskId) updatedTask else it }
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTask(taskId: String, classCode: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                apiService.deleteTask(classCode, taskId)
                _tasks.value = _tasks.value?.filter { it.id != taskId }
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                _loading.value = false
            }
        }
    }
}
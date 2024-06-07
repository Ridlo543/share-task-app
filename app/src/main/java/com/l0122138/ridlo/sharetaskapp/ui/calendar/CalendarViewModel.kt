package com.l0122138.ridlo.sharetaskapp.ui.calendar

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.l0122138.ridlo.sharetaskapp.model.TaskData

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val _tasks = MutableLiveData<List<TaskData>>()
    val tasks: LiveData<List<TaskData>> get() = _tasks

    init {
        loadTasksFromSharedPreferences()
    }

    private fun loadTasksFromSharedPreferences() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("MyTasks", Context.MODE_PRIVATE)
        val allTasks = mutableListOf<TaskData>()
        for (entry in sharedPreferences.all) {
            val jsonTasks = entry.value as? String
            val tasks = Gson().fromJson(jsonTasks, Array<TaskData>::class.java).toList()
            allTasks.addAll(tasks)
        }
        _tasks.value = allTasks
    }
}
package com.l0122138.ridlo.sharetaskapp.ui.playground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TimerViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    private val _isPaused = MutableLiveData<Boolean>()
    val isPaused: LiveData<Boolean> = _isPaused

    private var timerJob: Job? = null
    private var remainingTime: Long = 0

    init {
        _timeLeft.value = 0
        _isRunning.value = false
        _isPaused.value = false
    }

    fun startTimer(initialTime: Long) {
        _timeLeft.value = initialTime
        remainingTime = initialTime
        _isRunning.value = true
        _isPaused.value = false
        startJob()
    }

    fun playTimer() {
        _isRunning.value = true
        _isPaused.value = false
        startJob()
    }

    fun pauseTimer() {
        _isRunning.value = false
        _isPaused.value = true
        timerJob?.cancel()
    }

    fun stopTimer() {
        _isRunning.value = false
        _isPaused.value = false
        timerJob?.cancel()
        _timeLeft.value = 0
    }

    private fun startJob() {
        timerJob = viewModelScope.launch {
            while (_timeLeft.value!! > 0 && _isRunning.value == true) {
                delay(1000)
                _timeLeft.value = _timeLeft.value?.minus(1)
            }
            _isRunning.value = false
        }
    }
}
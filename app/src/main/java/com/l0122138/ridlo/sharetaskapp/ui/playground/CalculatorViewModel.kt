package com.l0122138.ridlo.sharetaskapp.ui.playground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val _result = MutableLiveData<Double>()
    val result: LiveData<Double> = _result

    fun add(num1: Double, num2: Double) {
        _result.value = num1 + num2
    }

    fun subtract(num1: Double, num2: Double) {
        _result.value = num1 - num2
    }

    fun multiply(num1: Double, num2: Double) {
        _result.value = num1 * num2
    }

    fun divide(num1: Double, num2: Double) {
        if (num2 != 0.0) {
            _result.value = num1 / num2
        } else {
            _result.value = Double.NaN // Return NaN to indicate division by zero
        }
    }
}
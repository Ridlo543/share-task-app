package com.l0122138.ridlo.sharetaskapp.ui.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.l0122138.ridlo.sharetaskapp.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding
    private val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            val num1 = binding.firstNumber.text.toString().toDoubleOrNull()
            val num2 = binding.secondNumber.text.toString().toDoubleOrNull()
            if (num1 != null && num2 != null) {
                calculatorViewModel.add(num1, num2)
            }
        }

        binding.btnSubtract.setOnClickListener {
            val num1 = binding.firstNumber.text.toString().toDoubleOrNull()
            val num2 = binding.secondNumber.text.toString().toDoubleOrNull()
            if (num1 != null && num2 != null) {
                calculatorViewModel.subtract(num1, num2)
            }
        }

        binding.btnMultiply.setOnClickListener {
            val num1 = binding.firstNumber.text.toString().toDoubleOrNull()
            val num2 = binding.secondNumber.text.toString().toDoubleOrNull()
            if (num1 != null && num2 != null) {
                calculatorViewModel.multiply(num1, num2)
            }
        }

        binding.btnDivide.setOnClickListener {
            val num1 = binding.firstNumber.text.toString().toDoubleOrNull()
            val num2 = binding.secondNumber.text.toString().toDoubleOrNull()
            if (num1 != null && num2 != null) {
                calculatorViewModel.divide(num1, num2)
            }
        }

        calculatorViewModel.result.observe(viewLifecycleOwner) { result ->
            binding.result.text = result.toString()
        }
    }
}

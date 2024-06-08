package com.l0122138.ridlo.sharetaskapp.ui.playground

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.l0122138.ridlo.sharetaskapp.R

class KalkulatorFragment : Fragment() {

    companion object {
        fun newInstance() = KalkulatorFragment()
    }

    private val viewModel: KalkulatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_kalkulator, container, false)
    }
}
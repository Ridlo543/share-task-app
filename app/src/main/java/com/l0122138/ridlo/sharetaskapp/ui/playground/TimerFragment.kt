package com.l0122138.ridlo.sharetaskapp.ui.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            val inputTime = binding.inputTime.text.toString().toLongOrNull()
            if (inputTime != null && inputTime > 0) {
                timerViewModel.startTimer(inputTime)
            }
        }

        binding.pomodoroInfoButton.setOnClickListener {
            showPomodoroInfoBottomSheet()
        }

        binding.playButton.setOnClickListener {
            timerViewModel.playTimer()
        }

        binding.pauseButton.setOnClickListener {
            timerViewModel.pauseTimer()
        }

        binding.stopButton.setOnClickListener {
            timerViewModel.stopTimer()
        }

        timerViewModel.timeLeft.observe(viewLifecycleOwner) { timeLeft ->
            binding.timeDisplay.text = formatTime(timeLeft)
        }

        timerViewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.startButton.isEnabled = !isRunning
            binding.playButton.isEnabled = !isRunning
            binding.pauseButton.isEnabled = isRunning
        }

        timerViewModel.isPaused.observe(viewLifecycleOwner) { isPaused ->
            binding.playButton.isEnabled = isPaused
            binding.pauseButton.isEnabled = !isPaused
        }
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun showPomodoroInfoBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_pomodoro, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)
        dialog.show()
    }
}
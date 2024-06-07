package com.l0122138.ridlo.sharetaskapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Locale


class CalendarFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]

        calendarViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            markTaskDeadlines(tasks)
        }

        return view
    }

    private fun markTaskDeadlines(tasks: List<TaskData>) {
        val taskDates = tasks.mapNotNull { task ->
            val date = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            ).parse(task.deadline)
            date?.let {
                Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault()).toLocalDate()
            }
        }.map { CalendarDay.from(it) }

        // Menghapus dekorasi sebelumnya sebelum menambahkan yang baru
        calendarView.removeDecorators()

        // Menambahkan dekorasi untuk tugas
        val color = ContextCompat.getColor(requireContext(), R.color.red) // Menggunakan warna dari resource
        calendarView.addDecorator(EventDecorator(color, taskDates))
    }
}

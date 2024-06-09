package com.l0122138.ridlo.sharetaskapp.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.adapter.CalendarTaskAdapter
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Locale


class CalendarFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var taskAdapter: CalendarTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        taskRecyclerView = view.findViewById(R.id.taskCalendarRecyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = CalendarTaskAdapter(emptyList())
        taskRecyclerView.adapter = taskAdapter
        calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]

        calendarView.addDecorator(TextColorDecorator(requireContext()))

        calendarViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            markTaskDeadlines(tasks)
            updateTaskList(calendarView.currentDate)
        }

        calendarView.setOnMonthChangedListener { _, date ->
            updateTaskList(date)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        calendarViewModel.fetchTasks()
    }

    private fun markTaskDeadlines(tasks: List<TaskData>) {
        val taskDates = tasks.filter { !it.isDone }.mapNotNull { task ->
            val date = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            ).parse(task.deadline)
            date?.let {
                Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault()).toLocalDate()
            }
        }.map { CalendarDay.from(it) }

        calendarView.removeDecorators()

        val color = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
        calendarView.addDecorator(EventDecorator(color, taskDates))

        // Add TextColorDecorator only if current month is active
        if (calendarView.currentDate.month == CalendarDay.today().month) {
            calendarView.addDecorator(TextColorDecorator(requireContext()))
        }
    }

    private fun updateTaskList(date: CalendarDay) {
        val selectedMonth = date.date.monthValue
        val selectedYear = date.date.year
        val tasksForMonth = calendarViewModel.tasks.value?.filter { task ->
            val taskDate = Instant.parse(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
            taskDate.monthValue == selectedMonth && taskDate.year == selectedYear && !task.isDone
        } ?: emptyList()
        taskAdapter.updateTasks(tasksForMonth)
    }
}

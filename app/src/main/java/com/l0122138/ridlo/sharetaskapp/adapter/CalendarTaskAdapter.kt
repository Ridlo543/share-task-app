package com.l0122138.ridlo.sharetaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarTaskAdapter(private var tasks: List<TaskData>) :
    RecyclerView.Adapter<CalendarTaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskDeadline: TextView = itemView.findViewById(R.id.taskDeadline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.taskDeadline.text = formatDate(task.deadline)
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<TaskData>) {
        tasks = newTasks
        notifyItemRangeChanged(0, tasks.size)
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date: Date? = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) } ?: ""
    }
}
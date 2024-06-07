package com.l0122138.ridlo.sharetaskapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private var tasks: MutableList<TaskData>,
    private val taskActionListener: TaskActionListener
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    interface TaskActionListener {
        fun onEditTask(task: TaskData)
        fun onDeleteTask(taskId: String, position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskDeadline: TextView = itemView.findViewById(R.id.task_deadline)
        val moreOptionsTask: ImageView = itemView.findViewById(R.id.more_options_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskData = tasks[position]
        holder.taskName.text = taskData.name
        holder.taskDescription.text = taskData.description

        // Calculate time difference
        val deadline = taskData.deadline.toLocalDateTime()
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val duration = Duration.between(now, deadline)

        val days = duration.toDays()
        val hours = duration.minusDays(days).toHours()

        val context = holder.itemView.context
        holder.taskDeadline.text = context.getString(R.string.time_remaining, days, hours)

        holder.moreOptionsTask.setOnClickListener {
            showPopupMenu(it, taskData)
        }
    }

    override fun getItemCount() = tasks.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun setData(newList: List<TaskData>) {
        tasks.clear()
        tasks.addAll(newList.sortedBy { it.deadline.toLocalDateTime() })
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, taskData: TaskData) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_task_option)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit_task -> {
                    taskActionListener.onEditTask(taskData)
                    true
                }

                R.id.action_delete_task -> {
                    taskActionListener.onDeleteTask(taskData.id, tasks.indexOf(taskData))
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    fun removeItem(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.toLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return LocalDateTime.parse(this, formatter)
    }
}

package com.l0122138.ridlo.sharetaskapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
        fun onMarkTaskAsDone(task: TaskData)
        fun onMarkTaskAsNotDone(task: TaskData)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskDeadline: TextView = itemView.findViewById(R.id.task_deadline)
        val moreOptionsTask: Button = itemView.findViewById(R.id.more_options_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskData = tasks[position]
        holder.taskName.text = taskData.name
        holder.taskDescription.text = taskData.description

        if (!taskData.isDone) {
            // Calculate time difference only if task is not done
            val deadline = taskData.deadline.toLocalDateTime()
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val duration = Duration.between(now, deadline)

            val days = duration.toDays()
            val hours = duration.minusDays(days).toHours()

            val context = holder.itemView.context
            holder.taskDeadline.text = context.getString(R.string.time_remaining, days, hours)
        } else {
            // Clear the deadline text if task is done
            holder.taskDeadline.text = ""
        }

        holder.moreOptionsTask.setOnClickListener {
            showPopupMenu(it, taskData)
        }
    }

    override fun getItemCount() = tasks.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<TaskData>) {
        tasks.clear()
        tasks.addAll(newList.sortedBy { it.deadline.toLocalDateTime() })
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, taskData: TaskData) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_task_option)
        val markAsDoneMenuItem = popup.menu.findItem(R.id.action_mark_done)
        val markAsNotDoneMenuItem = popup.menu.findItem(R.id.action_mark_undone)

        // Show/Hide menu items based on task status
        markAsDoneMenuItem.isVisible = !taskData.isDone
        markAsNotDoneMenuItem.isVisible = taskData.isDone

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
                R.id.action_mark_done -> {
                    taskActionListener.onMarkTaskAsDone(taskData)
                    true
                }
                R.id.action_mark_undone -> {
                    taskActionListener.onMarkTaskAsNotDone(taskData)
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

    private fun String.toLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return LocalDateTime.parse(this, formatter)
    }
}

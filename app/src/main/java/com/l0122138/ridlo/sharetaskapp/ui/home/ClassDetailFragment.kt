package com.l0122138.ridlo.sharetaskapp.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.adapter.TaskAdapter
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClassDetailFragment : Fragment(), TaskAdapter.TaskActionListener {
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var fabMenuTask: FloatingActionMenu
    private lateinit var progressBarTask: CircularProgressIndicator
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var classDetailViewModel: ClassDetailViewModel
    private lateinit var taskDeadlineInput: TextInputEditText
    private lateinit var toggleButton: MaterialButtonToggleGroup
    private var classCode: String? = null
    private var className: String? = null
    private var showDoneTasks: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_class_detail, container, false)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView)
        fabAddTask = view.findViewById(R.id.fab_add_task)
        fabMenuTask = view.findViewById(R.id.fab_menu_task)
        progressBarTask = view.findViewById(R.id.progressBarTask)
        taskDeadlineInput = dialogView.findViewById(R.id.task_deadline_input)
        toggleButton = view.findViewById(R.id.toggleButton)

        taskDeadlineInput.setOnClickListener {
            showDateTimePicker(taskDeadlineInput)
        }

        taskRecyclerView.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(mutableListOf(), this)
        taskRecyclerView.adapter = taskAdapter

        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                showDoneTasks = when (checkedId) {
                    R.id.button_done -> true
                    R.id.button_not_done -> false
                    else -> showDoneTasks
                }
                filterTasks()
            }
        }

        classDetailViewModel = ViewModelProvider(this)[ClassDetailViewModel::class.java]

        classDetailViewModel.tasks.observe(viewLifecycleOwner) {
            filterTasks()
        }

        classDetailViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBarTask.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        arguments?.let {
            classCode = it.getString("classCode")
            className = it.getString("className")
            activity?.title = className
            classCode?.let { code -> classDetailViewModel.fetchTasks(code) }
        }

//        @Suppress("DEPRECATION")
//        setHasOptionsMenu(true)
        handleOnBackPressed()

        toggleButton.check(R.id.button_not_done)

        return view
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }

    private fun filterTasks() {
        val tasks = classDetailViewModel.tasks.value.orEmpty()
        val filteredTasks = tasks.filter { it.isDone == showDoneTasks }
        taskAdapter.setData(filteredTasks)
    }

    override fun onEditTask(task: TaskData) {
        showEditTaskDialog(task)
    }


    override fun onMarkTaskAsDone(task: TaskData) {
        val updatedTask = task.copy(isDone = true)
        classDetailViewModel.updateTask(
            task.id,
            classCode ?: "",
            updatedTask.name,
            updatedTask.description,
            updatedTask.deadline,
            updatedTask.isDone
        )
    }

    override fun onMarkTaskAsNotDone(task: TaskData) {
        val updatedTask = task.copy(isDone = false)
        classDetailViewModel.updateTask(
            task.id,
            classCode ?: "",
            updatedTask.name,
            updatedTask.description,
            updatedTask.deadline,
            updatedTask.isDone
        )
    }

//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        @Suppress("DEPRECATION")
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.class_item_menu, menu)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        @Suppress("DEPRECATION")
//        return when (item.itemId) {
//            android.R.id.home -> {
//                findNavController().navigateUp()
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val taskNameInput = dialogView.findViewById<EditText>(R.id.task_name_input)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val taskDeadlineInput = dialogView.findViewById<TextInputEditText>(R.id.task_deadline_input)

        taskDeadlineInput.setOnClickListener {
            showDateTimePicker(taskDeadlineInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = taskNameInput.text.toString()
                val description = taskDescriptionInput.text.toString()
                val deadline = taskDeadlineInput.text.toString() // Handle date parsing as needed
                classCode?.let { code ->
                    classDetailViewModel.addTask(code, name, description, deadline)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showDateTimePicker(taskDeadlineInput: TextInputEditText) {
        val currentDateTime = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = Calendar.getInstance()
                date.set(year, month, dayOfMonth)

                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        date.set(Calendar.MINUTE, minute)
                        date.set(Calendar.SECOND, 0)

                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        taskDeadlineInput.setText(dateFormat.format(date.time))
                    },
                    currentDateTime.get(Calendar.HOUR_OF_DAY),
                    currentDateTime.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showEditTaskDialog(task: TaskData) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val taskNameInput = dialogView.findViewById<EditText>(R.id.task_name_input)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val taskDeadlineInput = dialogView.findViewById<TextInputEditText>(R.id.task_deadline_input)

        taskNameInput.setText(task.name)
        taskDescriptionInput.setText(task.description)
        taskDeadlineInput.setText(task.deadline)

        taskDeadlineInput.setOnClickListener {
            showDateTimePicker(taskDeadlineInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val name = taskNameInput.text.toString()
                val description = taskDescriptionInput.text.toString()
                val deadline = taskDeadlineInput.text.toString()
                classCode?.let { code ->
                    classDetailViewModel.updateTask(
                        task.id,
                        code,
                        name,
                        description,
                        deadline,
                        task.isDone
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDeleteTask(taskId: String, position: Int) {
        showDeleteConfirmationDialog(taskId, position)
    }

    private fun deleteTask(taskId: String, position: Int) {
        classCode?.let { code ->
            classDetailViewModel.deleteTask(taskId, code)
            taskAdapter.removeItem(position)
        }
    }

    private fun showDeleteConfirmationDialog(taskId: String, position: Int) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_task_confirmation_title))
            .setMessage(getString(R.string.delete_task_confirmation_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteTask(taskId, position)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.show()
    }
}

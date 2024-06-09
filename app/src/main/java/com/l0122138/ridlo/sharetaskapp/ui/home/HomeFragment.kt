package com.l0122138.ridlo.sharetaskapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.l0122138.ridlo.sharetaskapp.MainActivity
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.adapter.ClassAdapter
import com.l0122138.ridlo.sharetaskapp.model.ClassData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), ClassAdapter.OnClassActionListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabMenu: FloatingActionMenu
    private lateinit var fabAddClass: FloatingActionButton
    private lateinit var fabCreateClass: FloatingActionButton
    private lateinit var progressBar: com.google.android.material.progressindicator.CircularProgressIndicator
    private lateinit var classAdapter: ClassAdapter
    private lateinit var classViewModel: ClassViewModel
    private lateinit var currentDateTime: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val current = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("HH:mm:ss\nEEEE, dd MMMM yyyy", Locale.getDefault())
            currentDateTime.text = dateFormat.format(current)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.classRecyclerView)
        fabMenu = view.findViewById(R.id.fab_menu)
        fabAddClass = view.findViewById(R.id.fab_add_class)
        fabCreateClass = view.findViewById(R.id.fab_create_class)
        progressBar = view.findViewById(R.id.progressBar)
        currentDateTime = view.findViewById(R.id.currentDateTime)

        recyclerView.layoutManager = LinearLayoutManager(context)
        classAdapter = ClassAdapter(mutableListOf(), this)
        recyclerView.adapter = classAdapter

        fabAddClass.setOnClickListener {
            showAddClassDialog()
        }

        fabCreateClass.setOnClickListener {
            showCreateClassDialog()
        }

        (activity as MainActivity).hideActionBar()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classViewModel = ViewModelProvider(this)[ClassViewModel::class.java]

        classViewModel.classes.observe(viewLifecycleOwner) { classes ->
            classAdapter.setData(classes)
        }

        classViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        classViewModel.fetchClasses()

        handler.post(updateTimeRunnable)
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as MainActivity).hideActionBar()
//    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).showActionBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // prevent memory leaks
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onClassClicked(classData: ClassData) {
        val defaultLabel = getString(R.string.class_detail_label)

        val classNameLabel = "$defaultLabel ${classData.name}"

        val navController = findNavController()
        navController.graph.findNode(R.id.classDetailFragment)?.label = classNameLabel

        val action =
            HomeFragmentDirections.actionNavigationHomeToClassDetailFragment(classData.code)
        navController.navigate(action)
    }

    private fun showAddClassDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Class Code")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_class, null)
        builder.setView(view)

        val inputLayout = view.findViewById<TextInputLayout>(R.id.input_layout)
        val inputEditText = view.findViewById<TextInputEditText>(R.id.input_edit_text)

        builder.setPositiveButton("Add") { dialog, _ ->
            val classCode = inputEditText.text.toString()
            if (classCode.isNotEmpty()) {
                classViewModel.fetchClassByCode(classCode)
            } else {
                inputLayout.error = "Class code cannot be empty"
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showCreateClassDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Class Name")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_class, null)
        builder.setView(view)

        val inputLayout = view.findViewById<TextInputLayout>(R.id.input_layout)
        val inputEditText = view.findViewById<TextInputEditText>(R.id.input_edit_text)

        builder.setPositiveButton("Create") { dialog, _ ->
            val className = inputEditText.text.toString()
            if (className.isNotEmpty()) {
                classViewModel.createClass(className)
            } else {
                inputLayout.error = "Class name cannot be empty"
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onUpdateClass(classData: ClassData) {
        showUpdateClassDialog(classData)
    }

    override fun onDeleteClass(classData: ClassData) {
        showDeleteClassConfirmationDialog(classData)
    }

    private fun showUpdateClassDialog(classData: ClassData) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Class Name")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(classData.name)
        builder.setView(input)

        builder.setPositiveButton("Update") { dialog, _ ->
            val updatedName = input.text.toString()
            if (updatedName.isNotEmpty()) {
                classViewModel.updateClass(classData.code, updatedName)
            } else {
                Toast.makeText(context, "Class name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showDeleteClassConfirmationDialog(classData: ClassData) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.delete_class_confirmation_title))
        builder.setMessage(getString(R.string.delete_class_confirmation_message))
        builder.setPositiveButton(getString(R.string.delete)) { dialog, _ ->
            classViewModel.deleteClass(classData.code)
            dialog.dismiss()
        }
        builder.setNegativeButton(
            getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}

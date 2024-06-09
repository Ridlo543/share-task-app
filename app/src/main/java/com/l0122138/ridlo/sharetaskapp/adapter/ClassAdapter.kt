package com.l0122138.ridlo.sharetaskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l0122138.ridlo.sharetaskapp.R
import com.l0122138.ridlo.sharetaskapp.model.ClassData

class ClassAdapter(
    private var classList: MutableList<ClassData>,
    private val listener: OnClassActionListener
) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    interface OnClassActionListener {
        fun onClassClicked(classData: ClassData)
        fun onUpdateClass(classData: ClassData)
        fun onDeleteClass(classData: ClassData)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.class_name)
        val classCode: TextView = itemView.findViewById(R.id.class_code)
        val moreOptions: Button = itemView.findViewById(R.id.more_options)

        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClassClicked(classList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classData = classList[position]
        holder.className.text = classData.name
        holder.classCode.text = classData.code

        holder.moreOptions.setOnClickListener {
            showPopupMenu(it, classData)
        }
    }

    override fun getItemCount() = classList.size

    fun setData(newList: List<ClassData>) {
        classList.clear()
        classList.addAll(newList.sortedBy { it.name })
        notifyItemRangeRemoved(0, classList.size)
        notifyItemRangeInserted(0, classList.size)
    }

    private fun showPopupMenu(view: View, classData: ClassData) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.class_item_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_update -> {
                    listener.onUpdateClass(classData)
                    true
                }

                R.id.action_delete -> {
                    listener.onDeleteClass(classData)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }
}
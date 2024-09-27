package com.example.prductivityapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<TaskModel>,
    private val onTaskUpdate: (TaskModel) -> Unit,
    private val onTaskDelete: (TaskModel) -> Unit,
    private val onTaskEdit: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskText: TextView = view.findViewById(R.id.task_text)
        val taskCheckbox: CheckBox = view.findViewById(R.id.task_checkbox)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskText.text = task.text
        holder.taskCheckbox.isChecked = task.isCompleted

        holder.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            onTaskUpdate(task) // Notify update callback
        }

        holder.deleteButton.setOnClickListener {
            onTaskDelete(task) // Notify delete callback
        }

        holder.taskText.setOnClickListener {
            onTaskEdit(task) // Notify edit callback
        }
    }

    override fun getItemCount() = tasks.size

    // Add a new task and notify adapter
    fun addTask(task: TaskModel) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    // Update an existing task and notify adapter
    fun updateTask(updatedTask: TaskModel) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
            notifyItemChanged(index)
        }
    }

    // Remove task by index
    fun removeTaskAt(index: Int) {
        tasks.removeAt(index)
        notifyDataSetChanged()

    }

    // Clear and update all tasks
    fun updateTasks(newTasks: List<TaskModel>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}

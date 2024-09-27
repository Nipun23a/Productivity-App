package com.example.prductivityapp




import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskInput: EditText
    private lateinit var addTaskButton: Button
    private lateinit var taskRecyclerView: RecyclerView
    private val tasks = mutableListOf<TaskModel>()

    companion object {
        fun newInstance(sessionManager: SessionManager): TaskFragment {
            val fragment = TaskFragment()
            fragment.sessionManager = sessionManager
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_task, container, false)

        taskInput = view.findViewById(R.id.task_input)
        addTaskButton = view.findViewById(R.id.add_task_btn)
        taskRecyclerView = view.findViewById(R.id.task_recycler_view)

        tasks.clear()
        tasks.addAll(sessionManager.getTasks())
        taskAdapter = TaskAdapter(
            tasks,
            onTaskUpdate = { task ->
                updateTask(task)
                sessionManager.saveTasks(tasks);
            },
            onTaskDelete = { task ->
                deleteTask(task)
                sessionManager.saveTasks(tasks);
            },
            onTaskEdit = { task ->
                showEditDialog(task)
                sessionManager.saveTasks(tasks);
            }
        )
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(context)

        addTaskButton.setOnClickListener {
            val taskText = taskInput.text.toString().trim()
            if (taskText.isNotEmpty()) {
                addTask(TaskModel(System.currentTimeMillis().toInt(), taskText))
                taskInput.text.clear()
            }
        }

        return view
    }

    private fun addTask(task: TaskModel) {
        tasks.add(task)
        taskAdapter.notifyItemInserted(tasks.size - 1)
        sessionManager.saveTasks(tasks)
    }

    private fun updateTask(task: TaskModel) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            taskAdapter.notifyItemChanged(index)
            sessionManager.saveTasks(tasks)
        }
    }

    private fun deleteTask(task: TaskModel) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks.removeAt(index)
            taskAdapter.notifyItemRemoved(index)
            taskAdapter.notifyItemRangeChanged(index, tasks.size)
            sessionManager.saveTasks(tasks)
        }
    }

    private fun showEditDialog(task: TaskModel) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Task")

        val input = EditText(requireContext())
        input.setText(task.text)
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val updatedText = input.text.toString().trim()
            if (updatedText.isNotEmpty()) {
                val updatedTask = task.copy(text = updatedText)
                updateTask(updatedTask)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}

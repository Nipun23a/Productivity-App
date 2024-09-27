package com.example.prductivityapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val reminders: List<ReminderModel>,
    private val onReminderClick: (ReminderModel) -> Unit,
    private val onReminderDelete: (ReminderModel) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reminderText: TextView = view.findViewById(R.id.reminder_item_text)
        val reminderTime: TextView = view.findViewById(R.id.reminder_item_time)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.reminderText.text = reminder.text
        holder.reminderTime.text = formatDate(reminder.time)

        holder.itemView.setOnClickListener {
            onReminderClick(reminder)
        }

        holder.deleteButton.setOnClickListener {
            onReminderDelete(reminder)
        }
    }

    override fun getItemCount() = reminders.size

    private fun formatDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }
}
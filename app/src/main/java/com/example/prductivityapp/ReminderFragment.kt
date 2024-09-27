package com.example.prductivityapp


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ReminderFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var reminderButton: Button
    private lateinit var reminderText: EditText
    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var reminders: MutableList<ReminderModel>

    companion object {
        fun newInstance(sessionManager: SessionManager): ReminderFragment {
            val fragment = ReminderFragment()
            fragment.sessionManager = sessionManager
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)

        reminderButton = view.findViewById(R.id.reminder_button)
        reminderText = view.findViewById(R.id.reminder_text)
        reminderRecyclerView = view.findViewById(R.id.reminders_list)

        reminderButton.setOnClickListener { showDateTimePicker() }

        // Load reminders from SessionManager
        reminders = sessionManager.getReminders().toMutableList()

        // Set up RecyclerView
        reminderAdapter = ReminderAdapter(
            reminders,
            onReminderClick = { reminder -> showUpdateDialog(reminder) },
            onReminderDelete = { reminder -> deleteReminder(reminder) }
        )
        reminderRecyclerView.adapter = reminderAdapter
        reminderRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    private fun showDateTimePicker() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                setReminder(pickedDateTime.timeInMillis)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun setReminder(timeInMillis: Long) {
        val newReminder = ReminderModel(
            System.currentTimeMillis().toInt(),
            reminderText.text.toString(),
            timeInMillis
        )
        reminders.add(newReminder)
        reminderAdapter.notifyItemInserted(reminders.size - 1)
        sessionManager.saveReminders(reminders)

        setAlarm(newReminder)

        reminderText.text.clear()
        Toast.makeText(context, "Reminder set successfully", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm(reminder: ReminderModel) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java).apply {
            putExtra("title", "Reminder")
            putExtra("message", reminder.text)
            putExtra("id", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.time,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.time, pendingIntent)
        }
    }

    private fun showUpdateDialog(reminder: ReminderModel) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Update Reminder")

        val input = EditText(requireContext())
        input.setText(reminder.text)
        dialog.setView(input)

        dialog.setPositiveButton("Update") { _, _ ->
            val updatedText = input.text.toString()
            if (updatedText.isNotEmpty()) {
                val updatedReminder = reminder.copy(text = updatedText)
                val index = reminders.indexOfFirst { it.id == reminder.id }
                if (index != -1) {
                    reminders[index] = updatedReminder
                    reminderAdapter.notifyItemChanged(index)
                    sessionManager.saveReminders(reminders)
                    setAlarm(updatedReminder)
                    Toast.makeText(context, "Reminder updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteReminder(reminder: ReminderModel) {
        // Cancel the alarm
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        // Remove from local list and update adapter
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders.removeAt(index)
            reminderAdapter.notifyItemRemoved(index)
            reminderAdapter.notifyItemRangeChanged(index, reminders.size)
        }

        // Update SessionManager
        sessionManager.saveReminders(reminders)

        Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show()
    }

}
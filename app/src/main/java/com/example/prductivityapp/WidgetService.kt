package com.example.prductivityapp

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.util.Date
import java.util.Locale

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetItemFactory(applicationContext)
    }
}

class WidgetItemFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var tasks: List<TaskModel> = listOf()
    private var reminders: List<ReminderModel> = listOf()

    override fun onCreate() {
        // Initialize data
    }

    override fun onDataSetChanged() {
        // Fetch latest data
        val sessionManager = SessionManager(context)
        tasks = sessionManager.getTasks()
        reminders = sessionManager.getReminders()
    }

    override fun onDestroy() {
        // Clean up if necessary
    }

    override fun getCount(): Int {
        return tasks.size + reminders.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_item)

        if (position < tasks.size) {
            val task = tasks[position]
            views.setTextViewText(R.id.widget_item_text, task.text)
            views.setTextViewText(R.id.widget_item_time, "Task")
        } else {
            val reminder = reminders[position - tasks.size]
            views.setTextViewText(R.id.widget_item_text, reminder.text)
            views.setTextViewText(R.id.widget_item_time, formatDate(reminder.time))
        }

        return views
    }

    private fun formatDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
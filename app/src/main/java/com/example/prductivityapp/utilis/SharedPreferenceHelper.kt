package com.example.prductivityapp.utilis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.prductivityapp.TaskModel

object SharedPreferencesHelper {
    private const val PREF_NAME = "TaskManagerPrefs"
    private const val KEY_TASKS = "tasks"

    fun saveTasks(context: Context, tasks: List<TaskModel>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_TASKS, tasks.joinToString("|") { "${it.id},${it.text},${it.isCompleted}" })
        editor.apply()
    }

    fun getTasks(context: Context): List<TaskModel> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val tasksString = prefs.getString(KEY_TASKS, "") ?: ""
        return if (tasksString.isNotEmpty()) {
            tasksString.split("|").map {
                val (id, text, isCompleted) = it.split(",")
                TaskModel(id.toInt(), text, isCompleted.toBoolean())
            }
        } else {
            emptyList()
        }
    }
}
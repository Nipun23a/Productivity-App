package com.example.prductivityapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTasks(tasks: List<TaskModel>) {
        val json = gson.toJson(tasks)
        sharedPreferences.edit().putString("tasks", json).apply()
    }

    fun getTasks(): List<TaskModel> {
        val json = sharedPreferences.getString("tasks", null)
        return if (json != null) {
            val type = object : TypeToken<List<TaskModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveReminders(reminders: List<ReminderModel>) {
        val json = gson.toJson(reminders)
        sharedPreferences.edit().putString("reminders", json).apply()
    }

    fun getReminders(): List<ReminderModel> {
        val json = sharedPreferences.getString("reminders", null)
        return if (json != null) {
            val type = object : TypeToken<List<ReminderModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
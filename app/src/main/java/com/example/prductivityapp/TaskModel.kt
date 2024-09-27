package com.example.prductivityapp

data class TaskModel(
    val id: Int,
    var text: String,
    var isCompleted: Boolean = false
)


package com.josiah.streaker.model

data class Habit(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val emoji: String = "🔥",
    val streak: Int = 1,
    val completedToday: Boolean = false,
    val category: String = "General"
)


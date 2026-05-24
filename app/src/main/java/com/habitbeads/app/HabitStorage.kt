package com.habitbeads.app

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun loadHabits(context: Context): List<Habit> {
    val raw = prefs(context).getString("habits", null) ?: return defaultHabits()
    return raw.lines().mapNotNull { line ->
        val parts = line.split("|")
        when (parts.size) {
            4 -> Habit(
                id = parts[0].toIntOrNull() ?: return@mapNotNull null,
                name = parts[1],
                subtitle = "",
                color = Color(parts[2].toIntOrNull() ?: return@mapNotNull null),
                target = parts[3].toIntOrNull() ?: 1
            )
            5 -> Habit(
                id = parts[0].toIntOrNull() ?: return@mapNotNull null,
                name = parts[1],
                subtitle = parts[2],
                color = Color(parts[3].toIntOrNull() ?: return@mapNotNull null),
                target = parts[4].toIntOrNull() ?: 1
            )
            else -> null
        }
    }.ifEmpty { defaultHabits() }
}

fun saveHabits(context: Context, habits: List<Habit>, nextId: Int) {
    fun clean(value: String) = value.replace("|", " ").replace("\n", " ").trim()
    val raw = habits.joinToString("\n") { "${it.id}|${clean(it.name)}|${clean(it.subtitle)}|${it.color.toArgb()}|${it.target}" }
    prefs(context).edit().putString("habits", raw).putInt("nextHabitId", nextId).apply()
}

fun loadNextHabitId(context: Context): Int = prefs(context).getInt("nextHabitId", 4)

fun loadCounts(context: Context): Map<String, Int> {
    val raw = prefs(context).getString("counts", "") ?: ""
    return raw.lines().mapNotNull { line ->
        val parts = line.split("=")
        if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: return@mapNotNull null) else null
    }.toMap()
}

fun saveCounts(context: Context, counts: Map<String, Int>) {
    prefs(context).edit().putString("counts", counts.entries.joinToString("\n") { "${it.key}=${it.value}" }).apply()
}

fun clearHabitStorage(context: Context) {
    prefs(context).edit().clear().apply()
}

private fun prefs(context: Context) = context.getSharedPreferences("habit_beads", Context.MODE_PRIVATE)

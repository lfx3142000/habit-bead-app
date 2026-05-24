package com.habitbeads.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val CellSize = 48.dp
private val HabitColumnWidth = 180.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HabitBeadsApp() }
    }
}

private data class Habit(val id: Int, val name: String, val color: Color, val target: Int = 1)

@Composable
private fun HabitBeadsApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) { HabitTrackerScreen() }
    }
}

@Composable
private fun HabitTrackerScreen() {
    val context = LocalContext.current
    var nextHabitId by remember { mutableIntStateOf(loadNextHabitId(context)) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    val habits = remember { mutableStateListOf<Habit>().apply { addAll(loadHabits(context)) } }
    val counts = remember { mutableStateMapOf<String, Int>().apply { putAll(loadCounts(context)) } }
    val days = remember { recentDays() }
    val horizontalScrollState = rememberScrollState()

    fun saveAll() {
        saveHabits(context, habits, nextHabitId)
        saveCounts(context, counts)
    }

    fun moveHabit(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in habits.indices || toIndex !in habits.indices) return
        val habit = habits.removeAt(fromIndex)
        habits.add(toIndex, habit)
        saveAll()
    }

    fun deleteHabit(habit: Habit) {
        habits.removeAll { it.id == habit.id }
        val prefix = "${habit.id}:"
        counts.keys.filter { it.startsWith(prefix) }.forEach { counts.remove(it) }
        saveAll()
    }

    fun resetAllData() {
        habits.clear()
        habits.addAll(defaultHabits())
        counts.clear()
        nextHabitId = 4
        prefs(context).edit().clear().apply()
        saveAll()
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Habit Beads", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Tap beads to log. Tap a habit title to edit. Long-press and drag the grip to reorder.", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showResetDialog = true }) { Text("Reset") }
                Button(onClick = { showAddDialog = true }) { Text("Add habit") }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(HabitColumnWidth)) {
                Spacer(modifier = Modifier.height(42.dp))
                habits.forEachIndexed { index, habit ->
                    HabitNameCell(
                        habit = habit,
                        canMoveUp = index > 0,
                        canMoveDown = index < habits.lastIndex,
                        onEdit = { habitToEdit = habit },
                        onDelete = { habitToDelete = habit },
                        onMoveUp = { moveHabit(index, index - 1) },
                        onMoveDown = { moveHabit(index, index + 1) }
                    )
                }
            }

            Column(modifier = Modifier.horizontalScroll(horizontalScrollState).fillMaxHeight()) {
                Row { days.forEach { DayHeader(it) } }
                LazyColumn {
                    items(habits.size) { rowIndex ->
                        val habit = habits[rowIndex]
                        Row {
                            days.forEach { day ->
                                val key = "${habit.id}:${day.dateKey}"
                                val count = counts[key] ?: 0
                                BeadCell(
                                    count = count,
                                    color = habit.color,
                                    isToday = day.isToday,
                                    onIncrement = {
                                        counts[key] = (count + 1).coerceAtMost(9)
                                        saveAll()
                                    },
                                    onDecrement = {
                                        val next = (count - 1).coerceAtLeast(0)
                                        if (next == 0) counts.remove(key) else counts[key] = next
                                        saveAll()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        HabitEditorDialog(
            title = "Add habit",
            initialHabit = null,
            confirmText = "Add",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) {
                    val id = nextHabitId
                    nextHabitId += 1
                    habits.add(Habit(id, trimmed, color, target = 1))
                    saveAll()
                }
                showAddDialog = false
            }
        )
    }

    habitToEdit?.let { habit ->
        HabitEditorDialog(
            title = "Edit habit",
            initialHabit = habit,
            confirmText = "Save",
            onDismiss = { habitToEdit = null },
            onConfirm = { name, color ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) {
                    val index = habits.indexOfFirst { it.id == habit.id }
                    if (index >= 0) {
                        habits[index] = habit.copy(name = trimmed, color = color)
                        saveAll()
                    }
                }
                habitToEdit = null
            }
        )
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete habit?") },
            text = { Text("Delete ${habit.name} and its saved bead history? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    deleteHabit(habit)
                    habitToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { OutlinedButton(onClick = { habitToDelete = null }) { Text("Cancel") } }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all data?") },
            text = { Text("This will restore the sample habits and delete all saved bead history.") },
            confirmButton = { TextButton(onClick = { resetAllData(); showResetDialog = false }) { Text("Reset") } },
            dismissButton = { OutlinedButton(onClick = { showResetDialog = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitNameCell(
    habit: Habit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var dragDistance by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var didDrag by remember { mutableStateOf(false) }
    val rowBackground = if (isDragging) {
        habit.color.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    Row(
        modifier = Modifier
            .height(CellSize)
            .fillMaxWidth()
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .combinedClickable(onClick = {
                if (didDrag) {
                    didDrag = false
                } else {
                    onEdit()
                }
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DragGrip(
            modifier = Modifier.pointerInput(habit.id, canMoveUp, canMoveDown) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        dragDistance = 0f
                        isDragging = true
                        didDrag = true
                    },
                    onDragEnd = {
                        dragDistance = 0f
                        isDragging = false
                    },
                    onDragCancel = {
                        dragDistance = 0f
                        isDragging = false
                    },
                    onDrag = { _, dragAmount ->
                        dragDistance += dragAmount.y
                        if (dragDistance > 34f && canMoveDown) {
                            onMoveDown()
                            dragDistance = 0f
                        } else if (dragDistance < -34f && canMoveUp) {
                            onMoveUp()
                            dragDistance = 0f
                        }
                    }
                )
            }
        )
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(habit.color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            habit.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onDelete, modifier = Modifier.height(36.dp)) { Text("×") }
    }
}

@Composable
private fun DragGrip(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(28.dp).height(CellSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.75f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
private fun DayHeader(day: DayInfo) {
    val background = if (day.isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    Column(
        modifier = Modifier.width(CellSize).height(42.dp).padding(3.dp).clip(RoundedCornerShape(10.dp)).background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day.dayLabel, style = MaterialTheme.typography.labelSmall)
        Text(day.dateLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BeadCell(count: Int, color: Color, isToday: Boolean, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    val background = when {
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        count > 0 -> color.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    }
    Box(
        modifier = Modifier
            .size(CellSize)
            .padding(3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .combinedClickable(onClick = onIncrement, onLongClick = onDecrement),
        contentAlignment = Alignment.Center
    ) { BeadCluster(count, color) }
}

@Composable
private fun BeadCluster(count: Int, color: Color) {
    if (count == 0) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
        return
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        beadRows(count).forEach { rowCount ->
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(rowCount) { Box(modifier = Modifier.padding(1.dp).size(7.dp).clip(CircleShape).background(color)) }
            }
        }
    }
}

private fun beadRows(count: Int): List<Int> = when (count.coerceIn(0, 9)) {
    1 -> listOf(1); 2 -> listOf(2); 3 -> listOf(1, 2); 4 -> listOf(2, 2); 5 -> listOf(2, 1, 2)
    6 -> listOf(3, 3); 7 -> listOf(2, 3, 2); 8 -> listOf(3, 2, 3); 9 -> listOf(3, 3, 3)
    else -> emptyList()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitEditorDialog(
    title: String,
    initialHabit: Habit?,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit
) {
    var habitName by remember { mutableStateOf(initialHabit?.name ?: "") }
    var colorIndex by remember { mutableIntStateOf(habitColors.indexOf(initialHabit?.color).takeIf { it >= 0 } ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it.take(40) },
                    singleLine = true,
                    label = { Text("Habit name") }
                )
                Text("Color", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    habitColors.forEachIndexed { index, color ->
                        val borderColor = if (index == colorIndex) MaterialTheme.colorScheme.onSurface else color
                        Box(
                            modifier = Modifier
                                .size(if (index == colorIndex) 30.dp else 26.dp)
                                .clip(CircleShape)
                                .background(borderColor)
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(color)
                                .combinedClickable(onClick = { colorIndex = index })
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(habitName, habitColors[colorIndex]) }) { Text(confirmText) } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private data class DayInfo(val dateKey: String, val dayLabel: String, val dateLabel: String, val isToday: Boolean)

private fun recentDays(): List<DayInfo> {
    val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dayFormat = SimpleDateFormat("EEE", Locale.US)
    val dateFormat = SimpleDateFormat("d", Locale.US)
    val todayKey = keyFormat.format(Calendar.getInstance().time)
    return (13 downTo 0).map { daysAgo ->
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        val key = keyFormat.format(calendar.time)
        DayInfo(key, dayFormat.format(calendar.time).take(3), dateFormat.format(calendar.time), key == todayKey)
    }
}

private fun loadHabits(context: Context): List<Habit> {
    val raw = prefs(context).getString("habits", null) ?: return defaultHabits()
    return raw.lines().mapNotNull { line ->
        val parts = line.split("|")
        if (parts.size != 4) null else Habit(parts[0].toIntOrNull() ?: return@mapNotNull null, parts[1], Color(parts[2].toIntOrNull() ?: return@mapNotNull null), parts[3].toIntOrNull() ?: 1)
    }.ifEmpty { defaultHabits() }
}

private fun saveHabits(context: Context, habits: List<Habit>, nextId: Int) {
    val raw = habits.joinToString("\n") { "${it.id}|${it.name.replace("|", " ").replace("\n", " ")}|${it.color.toArgb()}|${it.target}" }
    prefs(context).edit().putString("habits", raw).putInt("nextHabitId", nextId).apply()
}

private fun loadNextHabitId(context: Context): Int = prefs(context).getInt("nextHabitId", 4)

private fun loadCounts(context: Context): Map<String, Int> {
    val raw = prefs(context).getString("counts", "") ?: ""
    return raw.lines().mapNotNull { line ->
        val parts = line.split("=")
        if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: return@mapNotNull null) else null
    }.toMap()
}

private fun saveCounts(context: Context, counts: Map<String, Int>) {
    prefs(context).edit().putString("counts", counts.entries.joinToString("\n") { "${it.key}=${it.value}" }).apply()
}

private fun prefs(context: Context) = context.getSharedPreferences("habit_beads", Context.MODE_PRIVATE)

private fun defaultHabits() = listOf(
    Habit(1, "Water", Color(0xFF277DA1), target = 1),
    Habit(2, "Stretch", Color(0xFF2A9D8F), target = 1),
    Habit(3, "Read", Color(0xFFB56576), target = 1)
)

private val habitColors = listOf(Color(0xFFE76F51), Color(0xFFF4A261), Color(0xFF2A9D8F), Color(0xFF43AA8B), Color(0xFF277DA1), Color(0xFF6D597A), Color(0xFFB56576))

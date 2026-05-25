package com.habitbeads.app

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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.habitbeads.app.data.DatabaseProvider
import com.habitbeads.app.data.HabitRepository
import kotlinx.coroutines.launch

private enum class AppThemeChoice(val label: String) {
    Warm("Warm"),
    Ocean("Ocean"),
    Forest("Forest"),
    Grape("Grape")
}

private val WarmColorScheme = lightColorScheme(
    primary = Color(0xFF277DA1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9EEF6),
    onPrimaryContainer = Color(0xFF12343F),
    secondary = Color(0xFF2A9D8F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF4EE),
    onSecondaryContainer = Color(0xFF123B35),
    tertiary = Color(0xFFB56576),
    onTertiary = Color.White,
    background = Color(0xFFFFFBF6),
    onBackground = Color(0xFF241F1B),
    surface = Color(0xFFFFFBF6),
    onSurface = Color(0xFF241F1B),
    surfaceVariant = Color(0xFFF1E6DA),
    onSurfaceVariant = Color(0xFF5A5148),
    outline = Color(0xFF8C7D70)
)

private val OceanColorScheme = lightColorScheme(
    primary = Color(0xFF176B87),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F1F4),
    onPrimaryContainer = Color(0xFF082F3A),
    secondary = Color(0xFF2C7DA0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F4FF),
    onSecondaryContainer = Color(0xFF16384A),
    tertiary = Color(0xFF61A5C2),
    background = Color(0xFFF7FCFF),
    onBackground = Color(0xFF172126),
    surface = Color(0xFFF7FCFF),
    onSurface = Color(0xFF172126),
    surfaceVariant = Color(0xFFE7F0F4),
    onSurfaceVariant = Color(0xFF45545B),
    outline = Color(0xFF71828A)
)

private val ForestColorScheme = lightColorScheme(
    primary = Color(0xFF386641),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDEAD7),
    onPrimaryContainer = Color(0xFF1B331F),
    secondary = Color(0xFF6A994E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAF4E4),
    onSecondaryContainer = Color(0xFF273B1C),
    tertiary = Color(0xFFA7C957),
    background = Color(0xFFFFFDF6),
    onBackground = Color(0xFF202217),
    surface = Color(0xFFFFFDF6),
    onSurface = Color(0xFF202217),
    surfaceVariant = Color(0xFFEEEBDD),
    onSurfaceVariant = Color(0xFF555244),
    outline = Color(0xFF817C6B)
)

private val GrapeColorScheme = lightColorScheme(
    primary = Color(0xFF6D597A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE3F2),
    onPrimaryContainer = Color(0xFF33283A),
    secondary = Color(0xFFB56576),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8E2E7),
    onSecondaryContainer = Color(0xFF4A2830),
    tertiary = Color(0xFFE56B6F),
    background = Color(0xFFFFFAFC),
    onBackground = Color(0xFF251F24),
    surface = Color(0xFFFFFAFC),
    onSurface = Color(0xFF251F24),
    surfaceVariant = Color(0xFFF2E7EC),
    onSurfaceVariant = Color(0xFF594D54),
    outline = Color(0xFF897A83)
)

@Composable
fun HabitBeadsApp() {
    var themeChoice by remember { mutableStateOf(AppThemeChoice.Warm) }
    val scheme = when (themeChoice) {
        AppThemeChoice.Warm -> WarmColorScheme
        AppThemeChoice.Ocean -> OceanColorScheme
        AppThemeChoice.Forest -> ForestColorScheme
        AppThemeChoice.Grape -> GrapeColorScheme
    }
    MaterialTheme(colorScheme = scheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HabitTrackerScreen(themeChoice = themeChoice, onThemeChoiceChange = { themeChoice = it })
        }
    }
}

@Composable
private fun HabitTrackerScreen(themeChoice: AppThemeChoice, onThemeChoiceChange: (AppThemeChoice) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember(context) {
        val database = DatabaseProvider.getDatabase(context)
        HabitRepository(database.habitDao(), database.habitEntryDao())
    }
    var isLoaded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    val habits = remember { mutableStateListOf<Habit>() }
    val counts = remember { mutableStateMapOf<String, Int>() }
    val days = remember { recentDays() }
    val horizontalScrollState = rememberScrollState()

    suspend fun reloadFromRoom() {
        habits.clear()
        habits.addAll(repository.loadHabits())
        counts.clear()
        counts.putAll(repository.loadCounts())
        isLoaded = true
    }

    LaunchedEffect(repository) {
        reloadFromRoom()
    }

    fun moveHabit(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in habits.indices || toIndex !in habits.indices) return
        val habit = habits.removeAt(fromIndex)
        habits.add(toIndex, habit)
        scope.launch { repository.saveHabitOrder(habits.toList()) }
    }

    fun deleteHabit(habit: Habit) {
        habits.removeAll { it.id == habit.id }
        val prefix = "${habit.id}:"
        counts.keys.filter { it.startsWith(prefix) }.forEach { counts.remove(it) }
        scope.launch { repository.archiveHabit(habit.id) }
    }

    fun resetAllData() {
        scope.launch {
            repository.resetToDefaults()
            reloadFromRoom()
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Habit Beads", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Tap beads to log. Tap a habit title to edit. Drag the grip to reorder.", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { showOptionsDialog = true }) { Text("Options") }
                Button(onClick = { showAddDialog = true }) { Text("Add habit") }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!isLoaded) {
            EmptyHabitState(onAddHabit = { })
        } else if (habits.isEmpty()) {
            EmptyHabitState(onAddHabit = { showAddDialog = true })
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.width(HabitColumnWidth)) {
                    Spacer(modifier = Modifier.height(38.dp))
                    habits.forEachIndexed { index, habit ->
                        HabitNameCell(
                            habit = habit,
                            canMoveUp = index > 0,
                            canMoveDown = index < habits.lastIndex,
                            onEdit = { habitToEdit = habit },
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
                                        habitName = habit.name,
                                        day = day,
                                        count = count,
                                        color = habit.color,
                                        isToday = day.isToday,
                                        onIncrement = {
                                            val next = (count + 1).coerceAtMost(9)
                                            counts[key] = next
                                            scope.launch { repository.saveCount(habit.id, day.dateKey, next) }
                                        },
                                        onDecrement = {
                                            val next = (count - 1).coerceAtLeast(0)
                                            counts[key] = next
                                            scope.launch { repository.saveCount(habit.id, day.dateKey, next) }
                                        }
                                    )
                                }
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
            onConfirm = { name, subtitle, color ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) {
                    scope.launch {
                        val added = repository.addHabit(trimmed, subtitle.trim(), color, habits.size)
                        habits.add(added)
                    }
                }
                showAddDialog = false
            },
            onRequestDelete = null
        )
    }

    habitToEdit?.let { habit ->
        HabitEditorDialog(
            title = "Edit habit",
            initialHabit = habit,
            confirmText = "Save",
            onDismiss = { habitToEdit = null },
            onConfirm = { name, subtitle, color ->
                val trimmed = name.trim()
                if (trimmed.isNotEmpty()) {
                    val index = habits.indexOfFirst { it.id == habit.id }
                    if (index >= 0) {
                        val updated = habit.copy(name = trimmed, subtitle = subtitle.trim(), color = color)
                        habits[index] = updated
                        scope.launch { repository.updateHabit(updated, index) }
                    }
                }
                habitToEdit = null
            },
            onRequestDelete = {
                habitToEdit = null
                habitToDelete = habit
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

    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = { Text("Options") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Theme", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeChoice.values().forEach { choice ->
                            if (choice == themeChoice) {
                                Button(onClick = { onThemeChoiceChange(choice) }) { Text(choice.label) }
                            } else {
                                OutlinedButton(onClick = { onThemeChoiceChange(choice) }) { Text(choice.label) }
                            }
                        }
                    }
                    Text("Debug tools are kept here so the daily tracker stays clean.", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = {
                        showOptionsDialog = false
                        showResetDialog = true
                    }) { Text("Reset sample data") }
                }
            },
            confirmButton = { TextButton(onClick = { showOptionsDialog = false }) { Text("Done") } }
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

@Composable
private fun EmptyHabitState(onAddHabit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(24.dp)
            .semantics { contentDescription = "No habits yet. Add a habit to start tracking." },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No habits yet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add a habit to start filling your bead grid. You can edit, reorder, or delete it later.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddHabit) { Text("Add first habit") }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitNameCell(
    habit: Habit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
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
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .semantics {
                contentDescription = buildString {
                    append("Habit ${habit.name}")
                    if (habit.subtitle.isNotBlank()) append(", ${habit.subtitle}")
                    append(". Tap to edit.")
                }
                role = Role.Button
            }
            .combinedClickable(onClick = {
                if (didDrag) didDrag = false else onEdit()
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DragGrip(
            habitName = habit.name,
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
                        if (dragDistance > 32f && canMoveDown) {
                            onMoveDown()
                            dragDistance = 0f
                        } else if (dragDistance < -32f && canMoveUp) {
                            onMoveUp()
                            dragDistance = 0f
                        }
                    }
                )
            }
        )
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(habit.color))
        Spacer(modifier = Modifier.width(7.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                habit.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (habit.subtitle.isNotBlank()) {
                Text(
                    habit.subtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DragGrip(habitName: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(24.dp)
            .height(CellSize)
            .semantics { contentDescription = "Reorder $habitName" },
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
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun DayHeader(day: DayInfo) {
    val background = if (day.isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    Column(
        modifier = Modifier
            .width(CellSize)
            .height(38.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .semantics { contentDescription = if (day.isToday) "Today, ${day.dayLabel} ${day.dateLabel}" else "${day.dayLabel} ${day.dateLabel}" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day.dayLabel, style = MaterialTheme.typography.labelSmall)
        Text(day.dateLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BeadCell(
    habitName: String,
    day: DayInfo,
    count: Int,
    color: Color,
    isToday: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val background = when {
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        count > 0 -> color.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    }
    Box(
        modifier = Modifier
            .size(CellSize)
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .semantics {
                contentDescription = "$habitName on ${day.dayLabel} ${day.dateLabel}: $count beads. Tap to add one. Long press to subtract one."
                role = Role.Button
            }
            .combinedClickable(onClick = onIncrement, onLongClick = onDecrement),
        contentAlignment = Alignment.Center
    ) { BeadCluster(count, color) }
}

@Composable
private fun BeadCluster(count: Int, color: Color) {
    if (count == 0) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
        return
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        beadRows(count).forEach { rowCount ->
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(rowCount) { Box(modifier = Modifier.padding(1.dp).size(6.dp).clip(CircleShape).background(color)) }
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
    onConfirm: (String, String, Color) -> Unit,
    onRequestDelete: (() -> Unit)?
) {
    var habitName by remember { mutableStateOf(initialHabit?.name ?: "") }
    var habitSubtitle by remember { mutableStateOf(initialHabit?.subtitle ?: "") }
    var colorIndex by remember { mutableIntStateOf(habitColors.indexOf(initialHabit?.color).takeIf { it >= 0 } ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it.take(60) },
                    singleLine = true,
                    label = { Text("Habit title") }
                )
                OutlinedTextField(
                    value = habitSubtitle,
                    onValueChange = { habitSubtitle = it.take(70) },
                    singleLine = true,
                    label = { Text("Subtitle, optional") }
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
                                .semantics { contentDescription = "Select habit color ${index + 1}" }
                                .combinedClickable(onClick = { colorIndex = index })
                        )
                    }
                }
                if (onRequestDelete != null) {
                    TextButton(onClick = onRequestDelete) { Text("Delete habit") }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(habitName, habitSubtitle, habitColors[colorIndex]) }) { Text(confirmText) } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

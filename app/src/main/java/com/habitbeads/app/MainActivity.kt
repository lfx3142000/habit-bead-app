package com.habitbeads.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitBeadsApp()
        }
    }
}

@Composable
private fun HabitBeadsApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Habit Beads",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "Fast visual habit tracking is being built here.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    repeat(7) { index ->
                        Surface(
                            modifier = Modifier.clip(CircleShape),
                            color = beadColors[index % beadColors.size]
                        ) {
                            Text(
                                text = " ",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private val beadColors = listOf(
    Color(0xFFE76F51),
    Color(0xFFF4A261),
    Color(0xFF2A9D8F),
    Color(0xFF43AA8B),
    Color(0xFF277DA1),
    Color(0xFF6D597A),
    Color(0xFFB56576)
)

package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class JournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JournalScreenUI()
        }
    }
}

//main Journal Screen UI
@Composable
fun JournalScreenUI() {
    // State management for the Notes input
    var skinNotes by remember { mutableStateOf("") }

    // State management for the Mood tracker
    var selectedMoodIndex by remember { mutableStateOf(0) }
    val moodOptions = listOf("Great", "Okay", "Tired", "Stressed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(text = "Daily Skin Journal", fontSize = 24.sp)
        Text(text = "Log your daily factors and observations.", fontSize = 16.sp)

        // Text Input Field
        TextField(
            value = skinNotes,
            onValueChange = { skinNotes = it }, // Update state on user input
            label = { Text("Daily Notes (Acne, Dryness, etc.)") },
            modifier = Modifier.fillMaxWidth() // Uses full width
        )

        // Mood Tracker Section
        Text(text = "How do you feel today?", fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))

        // Radio Button Group
        RadioButtonGroup(
            radioOptions = moodOptions,
            selected = selectedMoodIndex,
            onStateChanged = { newIndex ->
                selectedMoodIndex = newIndex // Update the state when a new button is clicked
            }
        )
    }
}

// Reusable Composable for Radio Button Groups
@Composable
fun RadioButtonGroup(
    radioOptions: List<String>,
    selected: Int,
    onStateChanged: (Int) -> Unit
) {
    // The list of radio buttons will be laid out in a Column
    Column {
        radioOptions.forEachIndexed { index, option ->
            // Each button and its text label is placed in a Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == index,
                    onClick = { onStateChanged(index) }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
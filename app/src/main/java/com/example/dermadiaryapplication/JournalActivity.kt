package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

// ------------------- ACTIVITY CLASS -------------------

class JournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Calling the scaffold, which contains the TopAppBar and the UI
            AppScaffold(title = "Daily Reflection", showBackArrow = true) { paddingModifier ->
                JournalScreenUI(paddingModifier)
            }
        }
    }
}

// ------------------- REUSABLE SCAFFOLD (For Back Navigation) -------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBackArrow: Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (showBackArrow) {
                        // The back button action: finish the current Activity
                        IconButton(onClick = {
                            (context as ComponentActivity).finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    )
}


// ------------------- UI Implementation -------------------

@Composable
fun JournalScreenUI(modifier: Modifier) {
    // State management for inputs
    var skinNotes by remember { mutableStateOf("") }
    var selectedMoodIndex by remember { mutableStateOf(0) }
    val moodOptions = listOf("Happy", "Neutral", "Stressed")
    var stressLevel by remember { mutableStateOf(5f) }
    var dietNotes by remember { mutableStateOf("") }
    var productsUsed by remember { mutableStateOf("") }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Horizontal padding for card containment
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between cards
    ) {

        // Title (Daily Reflection - How are you doing today?)
        Text(text = "How are you doing today?", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))


        // Card 1: Mood/Emotion
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "How are you feeling?", fontSize = 16.sp)
                RadioButtonGroup(moodOptions, selectedMoodIndex) { newIndex ->
                    selectedMoodIndex = newIndex
                }
            }
        }

        // Card 2: Stress Level
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Stress Level (0-10)", fontSize = 16.sp)

                Text(text = "${stressLevel.toInt()}",
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally))

                Slider(
                    value = stressLevel,
                    onValueChange = { stressLevel = it },
                    valueRange = 0f..10f,
                    steps = 9 // 11 possible values (0 to 10)
                )
            }
        }


        // Card 3: Diet Notes (TextField)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "What did you eat today?", fontSize = 16.sp)
                TextField(
                    value = dietNotes,
                    onValueChange = { dietNotes = it },
                    placeholder = { Text("Breakfast, lunch, dinner, snacks... Any trigger foods?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3 // Allows multiple lines for notes
                )
            }
        }

        // Card 4: Skincare Products Used (TextField)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Skincare Products Used", fontSize = 16.sp)
                TextField(
                    value = productsUsed,
                    onValueChange = { productsUsed = it },
                    placeholder = { Text("Cleanser, moisturizer, serum, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }

        // Save Button
        Button(
            onClick = { /* Save Logic  */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Save Daily Log", fontSize = 18.sp)
        }


        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ------------------- REUSABLE COMPONENTS -------------------

// Reusable Composable for Radio Button Groups
@Composable
fun RadioButtonGroup(
    radioOptions: List<String>,
    selected: Int,
    onStateChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        radioOptions.forEachIndexed { index, option ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = selected == index,
                    onClick = { onStateChanged(index) }
                )
                Text(text = option, fontSize = 12.sp)
            }
        }
    }
}
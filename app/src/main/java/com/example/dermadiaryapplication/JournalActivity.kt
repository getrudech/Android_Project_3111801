package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext

// ------------------- ACTIVITY CLASS -------------------

class JournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScaffold(title = "Daily Reflection", showBackArrow = true) { paddingModifier ->
                JournalScreenUI(paddingModifier)
            }
        }
    }
}

// ------------------- REUSABLE SCAFFOLD (For Back Navigation and Transparent Header) -------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBackArrow: Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    val context = LocalContext.current
    val softBackgroundColor = Color(0xFFF0F2F5)

    Scaffold(
        containerColor = softBackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                // FIX: Set TopAppBar to transparent container color
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    if (showBackArrow) {
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
    var selectedMoodIndex by remember { mutableStateOf(0) }
    val moodOptions = listOf("Happy", "Neutral", "Stressed")
    var stressLevel by remember { mutableStateOf(5f) }
    var dietNotes by remember { mutableStateOf("") }
    var productsUsed by remember { mutableStateOf("") }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "How are you doing today?",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp))

        // Card 1: Mood/Emotion
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "How are you feeling?", fontSize = 16.sp)
                RadioButtonGroup(moodOptions, selectedMoodIndex) { newIndex ->
                    selectedMoodIndex = newIndex
                }
            }
        }

        // Card 2: Stress Level (Slider)
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
                    steps = 9
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
                    placeholder = { Text("Breakfast, lunch, dinner, snacks...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }

        // Save Button
        Button(
            onClick = { /* Save Logic Here */ },
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

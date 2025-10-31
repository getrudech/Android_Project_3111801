package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

// main Journal Screen UI
@Composable
fun JournalScreenUI() {
    // State management for the Notes input
    var skinNotes by remember { mutableStateOf("") }

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

        // Text Input Field for Notes
        TextField(
            value = skinNotes,
            onValueChange = { skinNotes = it },
            label = { Text("Daily Notes (Acne, Dryness, etc.)") },
            modifier = Modifier.fillMaxSize(0.9f) //90% of the screen width
        )

        // We will add more input fields here in the next step.

    }
}
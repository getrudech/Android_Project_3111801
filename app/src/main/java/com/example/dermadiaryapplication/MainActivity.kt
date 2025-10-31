package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Calling AppScaffold (defined in JournalActivity.kt) to provide the navigation bar structure.
            // showBackArrow is false because this is the home screen.
            AppScaffold(title = "SkinJourney") { paddingModifier ->
                HomeScreenUI(this, paddingModifier)
            }
        }
    }
}

// Polished UI using Cards and Rows
@Composable
fun HomeScreenUI(activity: ComponentActivity, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Text(text = "SkinJourney",
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 16.dp))
        Text(text = "Your wellness companion",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp))

        // Motivational Tip Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "✨ Your skin regenerates most during sleep",
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryCard(title = "Total Entries", value = "0")
            SummaryCard(title = "Current Streak", value = "0 🔥")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Quick Actions",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // Quick Action Buttons (Explicit Intents to next Activities)
        QuickActionButton(
            text = "Take Photo",
            subtext = "Capture today's progress",
            onClick = {
                val intent = Intent(activity, CameraActivity::class.java)
                activity.startActivity(intent)
            }
        )
        QuickActionButton(
            text = "Log Daily Info",
            subtext = "Track your lifestyle",
            onClick = {
                val intent = Intent(activity, JournalActivity::class.java)
                activity.startActivity(intent)
            }
        )
        QuickActionButton(
            text = "View Insights",
            subtext = "Discover patterns",
            onClick = {
                val intent = Intent(activity, InsightsActivity::class.java)
                activity.startActivity(intent)
            }
        )
        Spacer(modifier = Modifier.height(16.dp)) // Final Spacer
    }
}

// Reusable Composable for Summary Cards
@Composable
fun SummaryCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

// Reusable Composable for Action Buttons
@Composable
fun QuickActionButton(text: String, subtext: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 4.dp),
        onClick = onClick // Enables the card itself to be clickable
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // a simple Text placeholder for the Icon , Icon(Icons.Filled...))
            Text(text = "📸", fontSize = 32.sp, modifier = Modifier.padding(end = 16.dp))
            Column {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
                Text(text = subtext, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
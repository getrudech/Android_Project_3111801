package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScaffold(title = "") { paddingModifier ->
                HomeScreenUI(this, paddingModifier)
            }
        }
    }
}

@Composable
fun HomeScreenUI(activity: ComponentActivity, modifier: Modifier) {
    val softBackgroundColor = Color(0xFFF0F2F5)
    val vibrantAccent = Color(0xFFE91E63) // Pink/Magenta
    val secondaryAccent = Color(0xFF03A9F4) // Blue
    val tertiaryAccent = Color(0xFF4CAF50) // Green

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(softBackgroundColor)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Custom Header
        Text(text = "DermaDiary",
            fontSize = 36.sp,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(top = 16.dp))
        Text(text = "Your Skin Health Journal",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp))

        // Motivational Tip Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Your skin regenerates most during sleep",
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryCard(title = "Total Entries", value = "0", modifier = Modifier.weight(1f).padding(end = 8.dp))
            SummaryCard(title = "Current Streak", value = "0 Day Streak", modifier = Modifier.weight(1f).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Quick Actions",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // Quick Action Buttons
        QuickActionButton(
            text = "Take Photo",
            subtext = "Capture today's progress",
            onClick = {
                val intent = Intent(activity, CameraActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.CameraAlt,
            accentColor = vibrantAccent
        )
        QuickActionButton(
            text = "Log Daily Info",
            subtext = "Track your lifestyle",
            onClick = {
                val intent = Intent(activity, JournalActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.Assignment,
            accentColor = secondaryAccent
        )
        QuickActionButton(
            text = "View Insights",
            subtext = "Discover patterns",
            onClick = {
                val intent = Intent(activity, InsightsActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.Timeline,
            accentColor = tertiaryAccent
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

@Composable
fun QuickActionButton(text: String, subtext: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                // Circular icon container
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.1f), shape = CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // ACTUAL ICON IMPLEMENTATION
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp),
                    tint = accentColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = text, style = MaterialTheme.typography.titleMedium)
                Text(text = subtext, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

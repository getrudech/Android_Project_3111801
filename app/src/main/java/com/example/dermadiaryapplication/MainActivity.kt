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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "") { paddingModifier ->
                    HomeScreenUI(this, paddingModifier)
                }
            }
        }
    }
}

@Composable
fun HomeScreenUI(activity: ComponentActivity, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Custom Header
        Text(text = "DermaDiary",
            fontSize = 36.sp,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp))
        Text(text = "Your Skin Health Journal",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp))

        // Motivational Tip Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "Your skin regenerates most during sleep",
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
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
            color = MaterialTheme.colorScheme.onSurface,
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
            accentColor = MaterialTheme.colorScheme.primary // Pink
        )
        QuickActionButton(
            text = "Log Daily Info",
            subtext = "Track your lifestyle",
            onClick = {
                val intent = Intent(activity, JournalActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.AutoMirrored.Filled.Assignment,
            accentColor = MaterialTheme.colorScheme.secondary // Blue
        )
        QuickActionButton(
            text = "View Insights",
            subtext = "Discover patterns",
            onClick = {
                val intent = Intent(activity, InsightsActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.Timeline,
            accentColor = MaterialTheme.colorScheme.tertiary // Green
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.1f), shape = CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp),
                    tint = accentColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
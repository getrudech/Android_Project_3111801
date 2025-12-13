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
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dermadiaryapplication.data.db.DermaDiaryDatabase
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory
import com.example.dermadiaryapplication.ui.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                // Setting up the MVVM dependencies for the entire Home screen flow
                val database = remember { DermaDiaryDatabase.getDatabase(applicationContext) }
                val repository = remember { JournalRepository(database.journalDao()) }
                val factory = remember { DermaDiaryViewModelFactory(repository) }

                AppScaffold(title = "") { paddingModifier ->
                    HomeScreenUI(
                        activity = this,
                        modifier = paddingModifier,
                        viewModel = viewModel(factory = factory)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenUI(activity: ComponentActivity, modifier: Modifier, viewModel: HomeViewModel) {
    val scrollState = rememberScrollState()

    // Watching the ViewModel's state to get live updates from the database
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "DermaDiary",
            fontSize = 36.sp,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp))
        Text(text = "Your Skin Health Journal",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp))

        // Daily Tip Card
        Card(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tip of the Day: Consistent sleep boosts cell regeneration!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats section placeholders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Displaying the LIVE value from the ViewModel
            SummaryCard(title = "Total Entries", value = uiState.totalEntries, modifier = Modifier.weight(1f).padding(end = 8.dp))
            // Displaying the LIVE value from the ViewModel
            SummaryCard(title = "Current Streak", value = uiState.currentStreak, modifier = Modifier.weight(1f).padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Quick Actions",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // Navigation Buttons (these can stay exactly as they were)
        QuickActionButton(
            text = "Take Photo",
            subtext = "Capture today's progress",
            onClick = {
                val intent = Intent(activity, CameraActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.CameraAlt,
            accentColor = MaterialTheme.colorScheme.primary
        )
        QuickActionButton(
            text = "Log Daily Info",
            subtext = "Track your lifestyle",
            onClick = {
                val intent = Intent(activity, JournalActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.AutoMirrored.Filled.Assignment,
            accentColor = MaterialTheme.colorScheme.secondary
        )
        QuickActionButton(
            text = "View Insights",
            subtext = "Discover patterns",
            onClick = {
                val intent = Intent(activity, InsightsActivity::class.java)
                activity.startActivity(intent)
            },
            icon = Icons.Filled.Timeline,
            accentColor = MaterialTheme.colorScheme.tertiary
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
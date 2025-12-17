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
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory
import com.example.dermadiaryapplication.ui.viewmodel.HomeViewModel
import com.example.dermadiaryapplication.ui.AppScaffold
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.SelfImprovement // For Stress/Brain
import androidx.compose.material.icons.filled.WaterDrop      // For Hydration
import androidx.compose.material.icons.filled.NightsStay     // For Sleep
import androidx.compose.ui.text.font.FontWeight // <-- NEW IMPORT


class InsightsActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val app = application as DermaDiaryApp
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Your Skin BFF", showBackArrow = true) { paddingModifier ->
                    InsightsScreenUI(paddingModifier, viewModel)
                }
            }
        }
    }
}

@Composable
fun InsightsScreenUI(modifier: Modifier, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Show loading state
        if (uiState.isLoading) {
            CircularProgressIndicator(Modifier.padding(top = 32.dp))
        }

        // CONDITION: Show placeholder UI only when NO entries exist (entryCount == 0)
        else if (uiState.entryCount == 0) {
            NotEnoughDataCard()

            // Restored placeholder cards for visual appeal when empty
            Text("What we're looking for:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)

            MockInsightCard(
                title = "Sleep & Skin Secrets",
                description = "I'm keeping an eye on how your beauty rest (or lack thereof) affects those pesky breakouts. Keep logging!",
                icon = Icons.Filled.NightsStay
            )

            MockInsightCard(
                title = "Your Stress Signals",
                description = "Let's figure out if your skin is reacting to your busy days. High stress usually means more oiliness!",
                icon = Icons.Filled.SelfImprovement
            )

        } else {
            // --- DYNAMIC INSIGHTS (Shown for 1 or more entries) ---
            val entries = uiState.allEntries
            val waterGoal = uiState.userProfile?.waterGoal ?: 8
            val userConcerns = uiState.userProfile?.skinType?.split(",")?.firstOrNull()?.trim() ?: "Skin Health"

            // FIX: Made the heading BOLDER
            Text(
                "Your Personal Skin Report Card:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )

            // Insight 1: Stress vs Log Count
            val averageStress = entries.map { it.stressLevel }.average().toInt()
            MockInsightCard(
                title = "Chill Out, Friend!",
                description = "Okay, your average stress score is currently $averageStress/10. Remember that feeling stressed is like throwing a party for your ${userConcerns}! Maybe try a five-minute meditation today?",
                icon = Icons.Filled.SelfImprovement
            )

            // Insight 2: Water Goal Consistency
            val consistentDays = entries.count { it.waterIntake >= waterGoal }
            MockInsightCard(
                title = "Are We Hydrated or What?",
                description = "You're smashing your water goal ($waterGoal glasses) on $consistentDays out of ${uiState.entryCount} days! Keep that H2O coming—your skin is drinking it up!",
                icon = Icons.Filled.WaterDrop
            )

            // Insight 3: Sleep Goal Check
            val avgSleep = entries.mapNotNull { it.sleepHours.takeIf { it > 0 } }.average().takeIf { it.isFinite() }
            val sleepTarget = uiState.userProfile?.sleepGoal ?: 8

            MockInsightCard(
                title = "Time for That Beauty Sleep",
                description = "Your average sleep is ${avgSleep?.let { "%.1f".format(it) } ?: "N/A"} hours, but your goal is $sleepTarget. Getting closer to your target hours is the easiest anti-aging secret!",
                icon = Icons.Filled.NightsStay
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun NotEnoughDataCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Warning, contentDescription = "Warning", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Text("We Need More Tea!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(
                "I'm ready to be your skin detective, but I need some data first! Log one journal entry to unlock your personalized insights.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MockInsightCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Display the Icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp).padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
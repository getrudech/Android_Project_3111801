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

class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Insights", showBackArrow = true) { paddingModifier ->
                    InsightsScreenUI(paddingModifier)
                }
            }
        }
    }
}

@Composable
fun InsightsScreenUI(modifier: Modifier) {
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

        // Not Enough Data Card (Shows up when no data is present)
        NotEnoughDataCard()

        // Mock Insight Card 1: Sleep Correlation
        MockInsightCard(
            title = "Sleep vs. Acne Trends",
            description = "Data suggests a clear link between sleep consistency and breakout frequency. More data needed for a definitive conclusion.",
            iconText = "🛌"
        )

        // Mock Insight Card 2: Stress Correlation
        MockInsightCard(
            title = "Stress Level Impact",
            description = "High stress days correlate with increased oiliness and redness in the T-zone.",
            iconText = "🧠"
        )

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
            Text("Not Enough Data", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(
                "Keep logging daily entries to see patterns and insights.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MockInsightCard(title: String, description: String, iconText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icon Placeholder
            Text(text = iconText, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 16.dp))

            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
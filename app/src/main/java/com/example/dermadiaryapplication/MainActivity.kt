package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dermadiaryapplication.ui.AppScaffold
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory
import com.example.dermadiaryapplication.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as DermaDiaryApp
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Check Onboarding status and launch the correct activity
        lifecycleScope.launch {
            val profile = app.profileRepository.loadProfile()

            if (profile == null || !profile.hasCompletedOnboarding) {
                startActivity(Intent(this@MainActivity, AuthorizationActivity::class.java))
                finish()
                return@launch
            }

            setContent {
                DermaDiaryTheme {
                    AppScaffold(title = "", showBackArrow = false) { paddingModifier ->
                        HomeScreenUI(
                            modifier = paddingModifier,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenUI(modifier: Modifier, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp), // Padding creates a subtle border/margin effect
        shape = MaterialTheme.shapes.large,
        // FIX: Use primaryContainer for the light pink border effect
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                //Inner background color set to standard background/surface
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                // FIX: Use primaryContainer for the light pink background
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // CUTE ICON (Star)
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Glow Icon",
                        // Use the accent color for the icon, which looks great on primaryContainer
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp).padding(end = 12.dp)
                    )
                    Column {
                        // FIX: Single, clean greeting
                        Text(
                            text = "Hey, ready to get that glow today!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            // FIX: Use onPrimaryContainer for high contrast text on light pink background
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        // FIX: Removed the subtext here
                    }
                }
            }

            // --- Stats Section ---
            Text(text = "Your Progress",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // INTEGRATION: Total Entries
                SummaryCard(
                    title = "Total Logs",
                    value = uiState.entryCount.toString(),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                // INTEGRATION: Water Goal
                SummaryCard(
                    title = "Water Goal",
                    value = "${profile?.waterGoal ?: 8} glasses",
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            // --- Quick Actions ---
            Text(text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // Navigation Buttons using Explicit Intents
            QuickActionButton(
                text = "Take Photo",
                subtext = "Capture today's visual progress",
                onClick = { context.startActivity(Intent(context, CameraActivity::class.java)) },
                icon = Icons.Filled.CameraAlt,
                accentColor = MaterialTheme.colorScheme.tertiary
            )
            QuickActionButton(
                text = "Log Daily Info",
                subtext = "Track your lifestyle and routine",
                onClick = { context.startActivity(Intent(context, JournalActivity::class.java)) },
                icon = Icons.AutoMirrored.Filled.Assignment,
                accentColor = MaterialTheme.colorScheme.secondary
            )
            QuickActionButton(
                text = "View Insights",
                subtext = "Check your personalized skin report",
                onClick = { context.startActivity(Intent(context, InsightsActivity::class.java)) },
                icon = Icons.Filled.Timeline,
                accentColor = MaterialTheme.colorScheme.primary
            )
            QuickActionButton(
                text = "View Profile",
                subtext = "Check and update your goals",
                onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) },
                icon = Icons.Filled.Person,
                accentColor = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// SummaryCard
@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// QuickActionButton
@Composable
fun QuickActionButton(text: String, subtext: String, onClick: () -> Unit, icon: ImageVector, accentColor: Color) {
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
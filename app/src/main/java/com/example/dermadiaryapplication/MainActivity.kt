package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory
import com.example.dermadiaryapplication.ui.viewmodel.HomeViewModel
import com.example.dermadiaryapplication.ui.AppScaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color // Used for Color.Transparent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Declare ViewModel and Factory
    private lateinit var viewModel: HomeViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- REPOSITORY/FACTORY INITIALIZATION ---
        val app = application as DermaDiaryApp
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)

        // Initialize the ViewModel using ViewModelProvider (FIX for the 'viewModels' error)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        // Check Onboarding status and launch the correct activity
        lifecycleScope.launch {
            val profile = app.profileRepository.loadProfile()

            // --- START FIX FOR NULLABLE RECEIVER ---

            // Case 1: No profile exists (first run, or unregistered). Go to Auth.
            if (profile == null) {
                startActivity(Intent(this@MainActivity, AuthorizationActivity::class.java))
                finish()
                return@launch
            }

            // Case 2: Profile exists, but Onboarding is NOT complete. Go to Onboarding.
            if (!profile.hasCompletedOnboarding) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish() // Close this activity
                return@launch
            }
            // --- END FIX ---


            // If onboarding is complete (Case 3), set the content for MainActivity
            setContent {
                DermaDiaryTheme {
                    AppScaffold(title = "Dashboard", showBackArrow = false) { paddingModifier ->
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

// Placeholder UI for the main dashboard screen (UNCHANGED)
@Composable
fun HomeScreenUI(modifier: Modifier, viewModel: HomeViewModel) {
    val context = LocalContext.current

    // This is a placeholder for your main dashboard UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // You would use viewModel.uiState.collectAsState() here to get user data
        Text("Welcome back, User!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { context.startActivity(Intent(context, JournalActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Go to Journal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { context.startActivity(Intent(context, InsightsActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("View Insights")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("View Profile")
        }
    }
}
package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.ui.AppScaffold
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory
import com.example.dermadiaryapplication.ui.viewmodel.ProfileViewModel

class ProfileActivity : ComponentActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as DermaDiaryApp
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Your Profile", showBackArrow = true) { paddingModifier ->
                    ProfileScreenUI(
                        modifier = paddingModifier,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreenUI(modifier: Modifier, viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current as ComponentActivity
    val scrollState = rememberScrollState()

    // --- Sign Out Navigation Effect
    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            val intent = Intent(context, AuthorizationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            viewModel.signOutHandled()
        }
    }

    // Function to handle Edit action
    val onEditClicked: () -> Unit = {
        // Launch the OnboardingActivity to allow editing/overwriting of profile data
        val intent = Intent(context, OnboardingActivity::class.java)
        context.startActivity(intent)
    }

    Box(modifier = modifier.fillMaxSize()) {

        // Scrollable Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 80.dp), // Padding for the fixed button bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.padding(top = 32.dp))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 32.dp))
            } else {
                val p = uiState.profile

                // --- 1. Account & Status Section ---
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Account Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Divider(Modifier.padding(vertical = 8.dp))
                        ProfileDetailItem(title = "Username", value = p.username)
                        ProfileDetailItem(title = "Gender", value = p.gender.ifBlank { "Not set" })
                        ProfileDetailItem(
                            title = "Status",
                            value = if (p.hasCompletedOnboarding) "Setup Complete" else "Setup Incomplete",
                            isStatus = true
                        )
                    }
                }

                // --- 2. Goals Section ---
                ProfileDetailsCard(
                    title = "My Goals",
                    content = {
                        ProfileDetailItem(title = "Sleep Goal", value = "${p.sleepGoal} hours")
                        ProfileDetailItem(title = "Water Goal", value = "${p.waterGoal} glasses")
                    }
                )

                // --- 3. Skincare Profile ---
                ProfileDetailsCard(
                    title = "Skincare Profile",
                    content = {
                        Text("Primary Concerns:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(p.skinType.ifEmpty { "None selected." }, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        Text("Products Logged:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            p.productRoutine.replace(" | ", "\n").ifEmpty { "No routine logged." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Pre-existing Conditions:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(p.preexistingConditions.ifEmpty { "None specified." }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface) // Solid background for bar
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Edit Profile Button
            OutlinedButton(
                onClick = onEditClicked,
                modifier = Modifier.weight(1f).height(56.dp),
                enabled = !uiState.isLoading,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Edit Profile", color = MaterialTheme.colorScheme.primary)
            }

            // Sign Out Button
            Button(
                onClick = viewModel::signOut,
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !uiState.isLoading
            ) {
                Text("Sign Out", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

@Composable
fun ProfileDetailItem(title: String, value: String, isStatus: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isStatus && value.contains("Complete")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper Composable for consistent card design
@Composable
fun ProfileDetailsCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Divider(Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}
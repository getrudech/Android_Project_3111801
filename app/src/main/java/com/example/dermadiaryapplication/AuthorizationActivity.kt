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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.ui.AppScaffold
import com.example.dermadiaryapplication.OnboardingActivity
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.AuthViewModel
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory

class AuthorizationActivity : ComponentActivity() {

    // Use explicit declaration to avoid the 'by viewModels' delegate issues
    private lateinit var viewModel: AuthViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the factory and ViewModel (must be done before setContent)
        val app = application as DermaDiaryApp
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Sign In / Sign Up") { paddingModifier ->
                    AuthorizationScreenUI(
                        modifier = paddingModifier,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorizationScreenUI(
    modifier: Modifier,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Local state for dialogs and inputs
    var showSignInDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigation Effect: Triggers when ViewModel indicates a successful transition
    LaunchedEffect(uiState.navigateToOnboarding, uiState.navigateToHome) {
        if (uiState.navigateToOnboarding) {
            context.startActivity(Intent(context, OnboardingActivity::class.java))
            (context as ComponentActivity).finish()
            viewModel.navigationHandled()
        }
        if (uiState.navigateToHome) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as ComponentActivity).finish()
            viewModel.navigationHandled()
        }
    }

    // Error Dialog
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(uiState.error!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }

    // Sign In Dialog
    if (showSignInDialog) {
        AuthDialog(
            title = "Sign In",
            onDismiss = { showSignInDialog = false },
            onConfirm = { usr, pwd ->
                viewModel.signIn(usr, pwd)
                // Dialog hides itself upon successful navigation or is replaced by error dialog
            },
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            isLoading = uiState.isLoading
        )
    }

    // Sign Up Dialog
    if (showSignUpDialog) {
        AuthDialog(
            title = "Sign Up",
            onDismiss = { showSignUpDialog = false },
            onConfirm = { usr, pwd ->
                viewModel.register(usr, pwd) // Calls the register function
                // Dialog hides itself upon successful navigation or is replaced by error dialog
            },
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            isLoading = uiState.isLoading
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Access Your DermaDiary",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                username = ""
                password = ""
                showSignInDialog = true
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading
        ) {
            Text("Sign In")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                username = ""
                password = ""
                showSignUpDialog = true
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !uiState.isLoading
        ) {
            Text("Create Account")
        }

        // Temporary button to navigate to the main screen for testing (unchanged)
        Spacer(Modifier.height(32.dp))
        TextButton(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as ComponentActivity).finish()
            },
            enabled = !uiState.isLoading
        ) {
            Text("Continue as Guest (Skip Auth)")
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

// Helper Composable for the Reusable Sign In/Sign Up Dialog
@Composable
fun AuthDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (username: String, password: String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password (Placeholder)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(username, password) },
                enabled = username.isNotBlank() && password.isNotBlank() && !isLoading
            ) {
                Text(if (isLoading) "Processing..." else title)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancel")
            }
        }
    )
}
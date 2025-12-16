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

// Placeholder UI for the Authorization Screen
@Composable
fun AuthorizationScreenUI(
    modifier: Modifier,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current

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

        // Placeholder buttons for sign-in/sign-up
        Button(
            onClick = { /* TODO: Implement sign-in logic */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Sign In")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* TODO: Implement sign-up logic */ },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Create Account")
        }

        // Temporary button to navigate to the main screen for testing
        Spacer(Modifier.height(32.dp))
        TextButton(
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as ComponentActivity).finish()
            }
        ) {
            Text("Continue as Guest (Skip Auth)")
        }
    }
}
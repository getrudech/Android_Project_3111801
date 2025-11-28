package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme

class AuthorizationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                AuthScreenUI()
            }
        }
    }

    @Composable
    fun AuthScreenUI() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Title
            Text(
                text = "DermaDiary",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Input Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use Theme Color
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface) // Use Theme Color
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(), // Hides the password input
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface) // Use Theme Color
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button (Navigates to Home - Simulating a successful login)
            Button(
                onClick = {
                    // In a real app, we would verify credentials here.
                    // For now, we assume login is successful and go to Home.
                    val intent = Intent(this@AuthorizationActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close Auth screen after login
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Use Theme Color
            ) {
                Text("Log In", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary) // Use Theme Color
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register Button (Navigates to Onboarding - Simulating a new user)
            TextButton(
                onClick = {
                    // Navigating to the Onboarding Questionnaire
                    val intent = Intent(this@AuthorizationActivity, OnboardingActivity::class.java)
                    startActivity(intent)
                }
            ) {
                Text("New here? Create an Account", color = MaterialTheme.colorScheme.primary) // Use Theme Color
            }
        }
    }
}
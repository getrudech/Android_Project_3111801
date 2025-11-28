package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                SplashScreenUI()
            }
        }
    }

    @Composable
    fun SplashScreenUI() {
        val coroutineScope = rememberCoroutineScope() // Coroutine scope for running tasks

        // LaunchedEffect triggers a coroutine immediately when the composable enters the screen
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                // Delay for 3 seconds
                delay(3000)

                // Navigate to the next screen (AuthActivity)
                val intent = Intent(this@SplashActivity, AuthorizationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // --- UI Layout ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DermaDiary",
                fontSize = 50.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Loading Your Skin Health Journal...",
                style = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Progress Bar (Dynamic Element)
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
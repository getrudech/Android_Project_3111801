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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                SplashScreenUI(this)
            }
        }
    }

    @Composable
    fun SplashScreenUI(activity: ComponentActivity) {

        // LaunchedEffect triggers a coroutine immediately when the composable enters the screen
        LaunchedEffect(key1 = true) {
            launch {
                // Delay for 3 seconds
                delay(3000)

                // Navigate to the next screen (AuthActivity)
                val intent = Intent(activity, AuthorizationActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
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
                //  Correct brand name and size
                text = "Derma Diary",
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Loading Your Skin Health Journal...",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
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
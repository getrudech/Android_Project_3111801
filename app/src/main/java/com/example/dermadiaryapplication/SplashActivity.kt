package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
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

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreenUI()
        }
    }

    @Composable
    fun SplashScreenUI() {
        val coroutineScope = rememberCoroutineScope() // Coroutine scope for running tasks
        val vibrantAccent = Color(0xFFE91E63)

        // LaunchedEffect triggers a coroutine immediately when the composable enters the screen
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                // Delay for 3 seconds before navigating to the next screen
                delay(3000)

                // Navigate to the next screen (AuthActivity)
                val intent = Intent(this@SplashActivity, AuthorizationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // UI Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Element Placeholder
            Text(
                text = "DermaDiary",
                fontSize = 50.sp,
                color = vibrantAccent,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Loading Your Skin Health Journal...",
                style = androidx.compose.ui.text.TextStyle(color = Color.Gray),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Progress Bar
            CircularProgressIndicator(
                color = vibrantAccent,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

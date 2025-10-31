package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenUI(this)
        }
    }
}

// UI structure
@Composable
fun HomeScreenUI(activity: ComponentActivity) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DermaDiary: Home",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
        )

        // The Journal Button Logic
        Button(onClick = {
            val journalIntent = Intent(activity, JournalActivity::class.java)
            activity.startActivity(journalIntent)
        }) {
            Text(text = "Start Daily Journal")
        }
    }
}
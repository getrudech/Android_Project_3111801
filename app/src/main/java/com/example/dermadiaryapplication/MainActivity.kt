package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Connect your buttons
        val btnJournal = findViewById<Button>(R.id.btnJournal)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnInsights = findViewById<Button>(R.id.btnInsights)

        // Button click actions
        btnJournal.setOnClickListener {
            Toast.makeText(this, "Journal will open soon", Toast.LENGTH_SHORT).show()
        }

        btnCamera.setOnClickListener {
            Toast.makeText(this, "Camera feature coming next", Toast.LENGTH_SHORT).show()
        }

        btnInsights.setOnClickListener {
            Toast.makeText(this, "Insights coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}


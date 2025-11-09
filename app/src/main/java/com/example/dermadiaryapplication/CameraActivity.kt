package com.example.dermadiaryapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

// Sensor Imports for Milestone 2
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext


// GLOBAL STATE and SENSOR LOGIC
private var lightValue = mutableStateOf(0.0f)
private lateinit var sensorManager: SensorManager
private var lightSensor: Sensor? = null

private val lightListener: SensorEventListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightValue.value = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            AppScaffold(title = "Take Photo", showBackArrow = true) { paddingModifier ->
                CameraScreenUI(paddingModifier)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(
                lightListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightListener)
    }

    @Composable
    fun CameraScreenUI(modifier: Modifier) {
        // IMPLICIT INTENT SETUP
        var imageUri by remember { mutableStateOf<Uri?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }

        // SENSOR DISPLAY LOGIC
        val currentLux = lightValue.value
        val lightingFeedback = when {
            currentLux < 50f -> "Too Dark"
            currentLux > 5000f -> "Too Bright"
            else -> "Good"
        }
        val softBackgroundColor = Color(0xFFF0F2F5)


        Column(
            modifier = modifier
                .fillMaxSize()
                .background(softBackgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Tips Card (Integrating Sensor feedback)
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Tips for best results", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("• Use natural lighting if possible")
                    Text("• Take photos at the same time each day")
                    Text("• Keep the same angle and distance")
                    Text("• Current Light: ${"%.1f".format(currentLux)} Lux (${lightingFeedback})")
                }
            }

            // 2. Photo Placeholder Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Button to trigger photo
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    ) {
                        // Placeholder for a Camera Icon
                        Text("Cam", fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Tap to take photo", style = MaterialTheme.typography.titleMedium)
                    Text("or upload from gallery", style = MaterialTheme.typography.bodySmall)

                    if (imageUri != null) {
                        Text("Image Ready", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Text("Your photos are stored securely and privately", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))

        }
    }
}

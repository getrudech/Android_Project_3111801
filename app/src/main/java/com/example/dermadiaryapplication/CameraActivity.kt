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
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext

// Sensor Imports
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// Variable to hold the light reading
private var lightValue = mutableStateOf(0.0f)
private lateinit var sensorManager: SensorManager
private var lightSensor: Sensor? = null

// Listener that receives sensor updates
private val lightListener: SensorEventListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightValue.value = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Required by the interface
    }
}

class CameraActivity : ComponentActivity() {

    // 1. LIFECYCLE: onCreate (Setup services and initial UI)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SensorManager service
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Get the default Light Sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            // Use the scaffold for the back button and title
            AppScaffold(title = "Take Photo", showBackArrow = true) { paddingModifier ->
                CameraScreenUI(paddingModifier)
            }
        }
    }

    // 2. LIFECYCLE: onResume (Register listener to start receiving data and save battery)
    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(
                lightListener,
                it,
                SensorManager.SENSOR_DELAY_UI // Recommended delay for UI updates
            )
        }
    }

    // 3. LIFECYCLE: onPause (Unregister listener to stop receiving data and save battery)
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightListener)
    }

    @Composable
    fun CameraScreenUI(modifier: Modifier) {
        // IMPLICIT INTENT SETUP (Example 33)
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Tips Card
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("💡 Tips for best results", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("• Use natural lighting if possible")
                    Text("• Take photos at the same time each day")
                    Text("• Keep the same angle and distance")
                    Text("• Current Light: ${"%.1f".format(currentLux)} Lux (${lightingFeedback})",
                        color = if (lightingFeedback == "Good") Color(0xFF1E88E5) else Color.Red) // Dynamic color feedback
                }
            }

            // 2. Dashed Border Placeholder
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
                    // Large Icon/Button to trigger photo
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.size(80.dp),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {
                        // Placeholder for a Camera Icon
                        Text("📸", fontSize = 32.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Tap to take photo", style = MaterialTheme.typography.titleMedium)
                    Text("or upload from gallery", style = MaterialTheme.typography.bodySmall)

                    // Show selected status
                    if (imageUri != null) {
                        Text("✅ Image Ready", color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            // Final Notes
            Text("Your photos are stored securely and privately", modifier = Modifier.padding(top = 8.dp))

        }
    }
}

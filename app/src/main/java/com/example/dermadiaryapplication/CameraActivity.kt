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
import androidx.compose.foundation.shape.CircleShape
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModelProvider // <-- NEW IMPORT
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.CameraViewModel // <-- NEW IMPORT
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory // <-- NEW IMPORT
import com.example.dermadiaryapplication.ui.AppScaffold // Assuming AppScaffold exists

// Global variables for sensor handling (UNCHANGED)
private var lightValue = mutableStateOf(0.0f)
private lateinit var sensorManager: SensorManager
private var lightSensor: Sensor? = null

// The listener that detects changes in light (UNCHANGED)
private val lightListener: SensorEventListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightValue.value = event.values[0] // Update our state with new Lux value
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

class CameraActivity : ComponentActivity() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- ViewModel Initialization ---
        val app = application as DermaDiaryApp // Assuming DermaDiaryApp exists
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(CameraViewModel::class.java)
        // ------------------------------------

        // Initialize sensor service (UNCHANGED)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Take Photo", showBackArrow = true) { paddingModifier ->
                    CameraScreenUI(paddingModifier, viewModel) // Pass ViewModel
                }
            }
        }
    }

    // Start listening when the app is active (UNCHANGED)
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

    // Stop listening to save battery when app is in background (UNCHANGED)
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightListener)
    }

    @Composable
    fun CameraScreenUI(modifier: Modifier, viewModel: CameraViewModel) { // Updated signature
        // Setup the Implicit Intent to launch the phone's camera gallery
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val uiState by viewModel.uiState.collectAsState() // Collect state

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
            if (uri != null) {
                viewModel.logPhotoCompletion() // <-- CALL TO LOG PHOTO ENTRY
            }
        }

        // Effect to show success message briefly
        LaunchedEffect(uiState.logSuccess) {
            if (uiState.logSuccess) {
                // Allows the UI to update with "Image Logged!"
                viewModel.logHandled()
            }
        }

        // Logic to determine if lighting is good or bad (UNCHANGED)
        val currentLux = lightValue.value
        val lightingFeedback = when {
            currentLux < 50f -> "Too Dark (Warning)"
            currentLux > 5000f -> "Too Bright (Warning)"
            else -> "Good"
        }

        // Change text color based on the warning (UNCHANGED)
        val sensorColor = when (lightingFeedback) {
            "Good" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }


        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Card showing tips and sensor feedback (UNCHANGED)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Tips for best results", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Text("• Use natural lighting if possible", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Take photos at the same time each day", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• Keep the same angle and distance", color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Displays the live sensor data
                    Text(
                        text = "• Light Level: ${"%.1f".format(currentLux)} Lux - ${lightingFeedback}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = sensorColor
                    )
                }
            }

            // Main Camera/Photo area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Button to trigger the implicit intent (UNCHANGED)
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    ) {
                        Text("Cam", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Tap to take photo", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("or upload from gallery", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Display status based on ViewModel state
                    if (uiState.isLogging) {
                        Text("Logging photo...", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                    } else if (imageUri != null && uiState.logSuccess) {
                        Text("Image Logged!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                    } else if (uiState.error != null) {
                        Text("Log Error: ${uiState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    } else if (imageUri != null) {
                        Text("Image Selected...", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Text("Your photos are stored securely and privately", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)

        }
    }
}
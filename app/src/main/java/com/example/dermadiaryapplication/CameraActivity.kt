package com.example.dermadiaryapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.activity.compose.rememberLauncherForActivityResult
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableFloatStateOf

//defining sensor listener
private var lightValue = mutableFloatStateOf(0.0f) // State to hold the Lux reading
private lateinit var sensorManager: SensorManager
private var lightSensor: Sensor? = null

// Listener that receives sensor updates
private val lightListener: SensorEventListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightValue.floatValue = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
class CameraActivity : ComponentActivity() {
    // 1.LIFECYCLE: onCreate (Setup services and initial UI)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SensorManager service
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Get the default Light Sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        setContent {
            CameraScreenUI()
        }
    }
    // 2.LIFECYCLE: onResume (Register listener to start receiving data and save battery)
    // Sensor data is requested when the activity is visible (onStart is called before onResume)
    override fun onResume() {
        super.onResume()
        // Register the listener when the activity is visible (saves battery)
        lightSensor?.let {
            sensorManager.registerListener(
                lightListener,
                it,
                SensorManager.SENSOR_DELAY_UI // Recommended delay for UI updates
            )
        }
    }
    // 3. LIFECYCLE: onPause (Unregister listener to stop receiving data and save battery)
    // Sensor data consumption is stopped when the activity is hidden (onStop is called after onPause)
    override fun onPause() {
        super.onPause()
        // Unregister the listener when the activity is paused
        sensorManager.unregisterListener(lightListener)
    }

@Composable
fun CameraScreenUI() {
    // STATE: Variable to hold the URI of the selected image
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Example 33

    // LAUNCHER: The object that handles launching the external app and receiving the resul
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    // Get the current Lux value (accessing the mutable state)
    val currentLux = lightValue.value

    // Determine the lighting feedback
    val lightingFeedback = when {
        currentLux < 50 -> "Lighting: Too Dark for a Good Photo"
        currentLux > 5000 -> "Lighting: Too Bright (Avoid Glare)"
        else -> "Lighting: Good"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Camera Capture", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        //BUTTON: Triggers the Implicit Intent
        Button(
            onClick = {
                // launcher.launch() initiates the intent.
                launcher.launch("image/*")
            }
        ) {
            Text(text = "Pick Image / Take Photo")
        }
        //Sensor Output and Feedback
        Text(text = "Current Light Level: ${"%.1f".format(currentLux)} Lux",
            modifier = Modifier.padding(top = 16.dp))
        Text(text = lightingFeedback, fontSize = 18.sp)
        //DISPLAY FEEDBACK
        if (imageUri != null) {
            Text(text = "Image URI captured successfully!")
            Text(text = "URI: ${imageUri.toString()}", modifier = Modifier.padding(top = 8.dp))
        } else {
            Text(text = "No image selected yet.")
        }
    }
}
}


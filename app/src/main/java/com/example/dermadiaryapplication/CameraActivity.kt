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

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreenUI()
        }
    }
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

        //DISPLAY FEEDBACK
        if (imageUri != null) {
            Text(text = "Image URI captured successfully!")
            Text(text = "URI: ${imageUri.toString()}", modifier = Modifier.padding(top = 8.dp))
        } else {
            Text(text = "No image selected yet.")
        }
    }
}
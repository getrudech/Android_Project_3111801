package com.example.dermadiaryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.data.db.DermaDiaryDatabase
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.ui.viewmodel.JournalViewModel
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory

class JournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                // Setting up the database and repository
                val database = remember { DermaDiaryDatabase.getDatabase(applicationContext) }
                val repository = remember { JournalRepository(database.journalDao()) }
                val factory = remember { DermaDiaryViewModelFactory(repository) }

                AppScaffold(title = "Daily Reflection", showBackArrow = true) { paddingModifier ->
                    // Injecting the ViewModel here so the UI can use it
                    JournalScreenUI(
                        modifier = paddingModifier,
                        viewModel = viewModel(factory = factory) // Tell Compose to use my custom factory
                    )
                }
            }
        }
    }
}

// Custom Scaffold wrapper to keep consistent headers
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    showBackArrow: Boolean = false,
    content: @Composable (Modifier) -> Unit
) {
    val context = LocalContext.current
    val scaffoldBackground = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = scaffoldBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = title, color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    if (showBackArrow) {
                        IconButton(onClick = {
                            (context as ComponentActivity).finish() // Close activity on back press
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            content(Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun JournalScreenUI(modifier: Modifier, viewModel: JournalViewModel) {
    // This watches the ViewModel for any state changes, so the UI updates automatically
    val uiState by viewModel.uiState.collectAsState()

    val moodOptions = listOf("Happy", "Neutral", "Stressed")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()), // Allows scrolling so keyboard doesn't hide fields
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "How are you doing today?", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onBackground)

        // Mood Selection
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "How are you feeling?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                // Hooking up the RadioButtonGroup to the ViewModel function
                RadioButtonGroup(moodOptions, uiState.selectedMoodIndex) {
                    viewModel.updateMood(it)
                }
            }
        }

        // Stress Slider
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Stress Level (0-10)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${uiState.stressLevel.toInt()}", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onSurface)
                Slider(
                    value = uiState.stressLevel,
                    onValueChange = { viewModel.updateStressLevel(it) },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )
            }
        }

        // Sleep Input
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Sleep Hours (Last Night)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = uiState.sleepHours,
                    onValueChange = { viewModel.updateSleepHours(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        // Water Input
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Water Glasses (Daily)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = uiState.waterGlasses,
                    onValueChange = { viewModel.updateWaterGlasses(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        // Diet Notes
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "What did you eat today?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = uiState.dietNotes,
                    onValueChange = { viewModel.updateDietNotes(it) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        // Product Checklist
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Skincare Products Used Today", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                SkincareCheckboxGroup(
                    products = uiState.skincareProducts,
                    checkedStates = uiState.productsUsedState,
                    onStateChanged = { index, isChecked ->
                        viewModel.updateProductUsed(index, isChecked)
                    }
                )
            }
        }

        Button(
            // Tying the button directly to the ViewModel's save function
            onClick = { viewModel.saveDailyLog() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Save Daily Log", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Helper composable for radio buttons
@Composable
fun RadioButtonGroup(radioOptions: List<String>, selected: Int, onStateChanged: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        radioOptions.forEachIndexed { index, option ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = selected == index,
                    onClick = { onStateChanged(index) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(text = option, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

// Helper composable for checkboxes
@Composable
fun SkincareCheckboxGroup(products: List<String>, checkedStates: List<Boolean>, onStateChanged: (index: Int, isChecked: Boolean) -> Unit) {
    Column {
        products.forEachIndexed { index, product ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = product, fontSize = 16.sp, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                Checkbox(
                    checked = checkedStates[index],
                    onCheckedChange = { isChecked -> onStateChanged(index, isChecked) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}
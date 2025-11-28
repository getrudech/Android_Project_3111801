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
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme

class JournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                AppScaffold(title = "Daily Reflection", showBackArrow = true) { paddingModifier ->
                    JournalScreenUI(paddingModifier)
                }
            }
        }
    }
}

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
                            (context as ComponentActivity).finish()
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
fun JournalScreenUI(modifier: Modifier) {
    var selectedMoodIndex by remember { mutableStateOf(0) }
    val moodOptions = listOf("Happy", "Neutral", "Stressed")
    var stressLevel by remember { mutableStateOf(5f) }
    var dietNotes by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf("8") }
    var waterGlasses by remember { mutableStateOf("8") }

    val skincareProducts = remember {
        listOf("Facial Cleanser", "Daily Moisturizer", "Vitamin C Serum", "Acne Spot Treatment")
    }
    var productsUsedState by remember {
        mutableStateOf(List(skincareProducts.size) { false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "How are you doing today?", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onBackground)

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "How are you feeling?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                RadioButtonGroup(moodOptions, selectedMoodIndex) { selectedMoodIndex = it }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Stress Level (0-10)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${stressLevel.toInt()}", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onSurface)
                Slider(value = stressLevel, onValueChange = { stressLevel = it }, valueRange = 0f..10f, steps = 9, colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary))
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Sleep Hours (Last Night)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = sleepHours,
                    onValueChange = { sleepHours = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Water Glasses (Daily)", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = waterGlasses,
                    onValueChange = { waterGlasses = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "What did you eat today?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextField(
                    value = dietNotes,
                    onValueChange = { dietNotes = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Skincare Products Used Today", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                SkincareCheckboxGroup(
                    products = skincareProducts,
                    checkedStates = productsUsedState,
                    onStateChanged = { index, isChecked ->
                        productsUsedState = productsUsedState.toMutableList().apply { this[index] = isChecked }
                    }
                )
            }
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Save Daily Log", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

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
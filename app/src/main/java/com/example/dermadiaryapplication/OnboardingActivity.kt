package com.example.dermadiaryapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // <-- Crucial missing import for LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider // <-- Crucial missing import for ViewModelProvider
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme
import com.example.dermadiaryapplication.ui.viewmodel.DermaDiaryViewModelFactory // <-- Crucial import for your Factory
import com.example.dermadiaryapplication.ui.viewmodel.OnboardingViewModel // <-- Crucial import for the ViewModel

class OnboardingActivity : ComponentActivity() {

    // 1. Must be declared at the class level for use in onCreate
    private lateinit var viewModel: OnboardingViewModel
    private lateinit var factory: DermaDiaryViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. The initialization block:
        val app = application as DermaDiaryApp // Resolves the 'DermaDiaryApp' reference
        factory = DermaDiaryViewModelFactory(app.journalRepository, app.profileRepository)
        viewModel = ViewModelProvider(this, factory).get(OnboardingViewModel::class.java)

        setContent {
            DermaDiaryTheme {
                // Pass the initialized ViewModel to the Composable
                OnboardingScreenUI(viewModel)
            }
        }
    }

    // Stores all the user's answers across the 3 steps (UNTOUCHED from your original code)
    class OnboardingData(
        val skinConcerns: SnapshotStateList<Boolean>,
        val productRoutine: SnapshotStateList<ProductInput>
    ) {
        var sleepGoal by mutableStateOf("8")
        var waterGoal by mutableStateOf("8")
        var gender by mutableStateOf("Prefer Not to Say")
        var preexistingConditions by mutableStateOf("")
    }

    // Helper class for the checklist items (UNTOUCHED from your original code)
    class ProductInput(
        val name: String
    ) {
        var isUsed by mutableStateOf(false)
        var brand by mutableStateOf("")
    }

    @Composable
    fun OnboardingScreenUI(viewModel: OnboardingViewModel) {
        val totalSteps = 3
        var currentStep by remember { mutableStateOf(0) }
        val productTypes = listOf("Cleanser", "Moisturizer", "Serum", "SPF")

        val onboardingData = remember {
            OnboardingData(
                skinConcerns = mutableStateListOf(false, false, false, false, false),
                productRoutine = mutableStateListOf<ProductInput>().apply {
                    productTypes.forEach { name -> add(ProductInput(name)) }
                }
            )
        }

        // Collect state from ViewModel
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current as ComponentActivity


        // Navigation Effect: Triggers when ViewModel indicates a successful save
        LaunchedEffect(uiState.saveSuccess) {
            if (uiState.saveSuccess) {
                // Navigate to the main dashboard
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                context.finish()
                viewModel.saveHandled() // Clear the flag
            }
        }

        // Error Dialog
        if (uiState.error != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Error") },
                text = { Text(uiState.error!!) },
                confirmButton = {
                    Button(onClick = { viewModel.clearError() }) { Text("OK") }
                }
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Visual progress bar at the top
            LinearProgressIndicator(
                progress = { (currentStep + 1) / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            // Dynamic content area: swaps screens based on step number
            Box(modifier = Modifier.weight(1f)) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    when (currentStep) {
                        0 -> SkinConcernsStep(onboardingData.skinConcerns)
                        1 -> RoutineStep(onboardingData.productRoutine)
                        2 -> LifestyleStep(onboardingData)
                    }
                }
            }

            // Navigation buttons (Back / Next)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    OutlinedButton(onClick = { currentStep-- }, enabled = !uiState.isSaving) {
                        Text("Back", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++ // Go to next step
                        } else {
                            // Finished! Call ViewModel to save data
                            viewModel.saveOnboardingData(onboardingData)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !uiState.isSaving // Disable button while saving
                ) {
                    Text(
                        when {
                            uiState.isSaving -> "Saving..."
                            currentStep == totalSteps - 1 -> "Finish"
                            else -> "Next"
                        },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    // NOTE: Keep your existing implementations of SkinConcernsStep, RoutineStep,
    // RoutineProductInput, and LifestyleStep here.

    @Composable
    fun SkinConcernsStep(checkedStates: SnapshotStateList<Boolean>) {
        val concerns = listOf("Acne/Breakouts", "Chronic Dryness", "Excess Oiliness", "Redness/Sensitivity", "Hyperpigmentation")

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("1/3: What are your main skin concerns?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("This helps us focus your daily tips.", color = MaterialTheme.colorScheme.onSurfaceVariant)

            concerns.forEachIndexed { index, concern ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(concern, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground)
                    Checkbox(
                        checked = checkedStates[index],
                        onCheckedChange = { isChecked ->
                            checkedStates[index] = isChecked
                        },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }

    @Composable
    fun RoutineStep(products: SnapshotStateList<ProductInput>) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("2/3: Tell us about your current skincare routine.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("We'll pre-tick these items in your daily journal.", color = MaterialTheme.colorScheme.onSurfaceVariant)

            products.forEachIndexed { index, product ->
                RoutineProductInput(product)
            }
        }
    }

    @Composable
    fun RoutineProductInput(product: ProductInput) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            // Checkbox for "Do you use this?"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = product.isUsed,
                    onCheckedChange = { isChecked ->
                        product.isUsed = isChecked
                        if (!isChecked) product.brand = "" // Clear text if unchecked
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text("Do you use a ${product.name}?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            }

            // Only show the text field if they actually use the product
            if (product.isUsed) {
                OutlinedTextField(
                    value = product.brand,
                    onValueChange = { product.brand = it },
                    label = { Text("Which brand/product?") },
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }

    @Composable
    fun LifestyleStep(data: OnboardingData) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("3/3: Your Lifestyle and Health History", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

            Text("What is your biological sex?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val genders = listOf("Female", "Male", "Prefer Not to Say")
                genders.forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = data.gender == gender,
                            onClick = { data.gender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(gender, modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            // Simple text inputs for goals
            OutlinedTextField(
                value = data.sleepGoal,
                onValueChange = { data.sleepGoal = it },
                label = { Text("Target Sleep Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)

            )

            OutlinedTextField(
                value = data.waterGoal,
                onValueChange = { data.waterGoal = it },
                label = { Text("Target Water Glasses (8oz)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
            )

            OutlinedTextField(
                value = data.preexistingConditions,
                onValueChange = { data.preexistingConditions = it },
                label = { Text("Pre-existing conditions (PCOS, Kidney, etc.)") },
                placeholder = { Text("Enter any relevant health conditions") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}
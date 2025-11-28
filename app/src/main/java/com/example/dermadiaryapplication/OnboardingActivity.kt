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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermadiaryapplication.ui.theme.DermaDiaryTheme

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaDiaryTheme {
                OnboardingScreenUI()
            }
        }
    }

    // --- Data Model for Questionnaire ---
    data class OnboardingData(
        val skinConcerns: SnapshotStateList<Boolean>,
        val productRoutine: SnapshotStateList<ProductInput>,
        var sleepGoal: String = "8",
        var waterGoal: String = "8",
        var gender: String = "Prefer Not to Say",
        var preexistingConditions: String = ""
    )

    // Note: This must be a mutable class to allow state changes to be observed by the SnapshotStateList
    data class ProductInput(
        val name: String,
        var isUsed: Boolean = false,
        var brand: String = ""
    )

    @Composable
    fun OnboardingScreenUI() {
        val totalSteps = 3
        var currentStep by remember { mutableStateOf(0) }
        val productTypes = listOf("Cleanser", "Moisturizer", "Serum", "SPF")

        // Primary state store for all answers
        val onboardingData = remember {
            OnboardingData(
                skinConcerns = mutableStateListOf(false, false, false, false, false), // Acne, Dry, Oily, Sensitive, Other
                productRoutine = mutableStateListOf<ProductInput>().apply {
                    productTypes.forEach { name -> add(ProductInput(name)) }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Progress Bar
            LinearProgressIndicator(
            progress = { (currentStep + 1) / totalSteps.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            // Dynamic Content based on Step
            Box(modifier = Modifier.weight(1f)) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(24.dp)) { // Added spacing
                    when (currentStep) {
                        0 -> SkinConcernsStep(onboardingData.skinConcerns)
                        1 -> RoutineStep(onboardingData.productRoutine)
                        2 -> LifestyleStep(onboardingData)
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button (Hide on first step)
                if (currentStep > 0) {
                    OutlinedButton(onClick = { currentStep-- }) {
                        Text("Back", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Next / Finish Button
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            // FINISH: Go to Home Dashboard
                            val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (currentStep == totalSteps - 1) "Finish" else "Next", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    // --- STEP 1: SKIN CONCERNS ---
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

    // --- STEP 2: ROUTINE ---
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

    // Specialized Composable for Conditional Product Input
    @Composable
    fun RoutineProductInput(product: ProductInput) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            // Checkbox: Do you use it?
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = product.isUsed,
                    onCheckedChange = { isChecked ->
                        // This makes the UI recompose and the checkbox state updates instantly.
                        product.isUsed = isChecked
                        if (!isChecked) product.brand = ""
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text("Do you use a ${product.name}?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            }

            // Conditional Text Field: Which brand?
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

    // --- STEP 3: LIFESTYLE ---
    @Composable
    fun LifestyleStep(data: OnboardingData) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("3/3: Your Lifestyle and Health History", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

            // Gender Input (Radio Buttons)
            Text("What is your biological sex?", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val genders = listOf("Female", "Male", "Prefer Not to Say")
                genders.forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = data.gender == gender,
                            onClick = { data.gender = gender }, // Direct mutation works here
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(gender, modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            // Sleep Goal Input
            OutlinedTextField(
                value = data.sleepGoal,
                onValueChange = { data.sleepGoal = it },
                label = { Text("Target Sleep Hours") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)

            )

            // Water Goal Input
            OutlinedTextField(
                value = data.waterGoal,
                onValueChange = { data.waterGoal = it },
                label = { Text("Target Water Glasses (8oz)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
            )

            // Pre-existing Conditions
            OutlinedTextField(
                value = data.preexistingConditions,
                onValueChange = { data.preexistingConditions = it }, // Direct mutation works here
                label = { Text("Pre-existing conditions (PCOS, Kidney, etc.)") },
                placeholder = { Text("Enter any relevant health conditions") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}
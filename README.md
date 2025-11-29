# DermaDiary 

**Your Personal Skin Health Journal**

DermaDiary is a native Android application designed to help users understand their skin better through daily tracking, smart camera updates, and lifestyle reflection. By connecting daily habits (sleep, diet, stress) with visual skin progress, DermaDiary acts as a holistic wellness companion.

> **Course:** Mobile Development (BSCH in Computer Science)  
> **Institution:** Griffith College Dublin  
> **Student:** Getrude Cherono

---

##  Features

* **Unified Authentication:** Seamless toggle between Login and Registration to get started quickly.
* **Smart Camera:** Uses the device's **Ambient Light Sensor** to provide real-time feedback (Too Dark / Good / Too Bright), ensuring consistent lighting for progress photos.
* **Personalized Onboarding:** A 3-step wizard that captures your specific skin concerns, product routine, and lifestyle goals.
* **Daily Journaling:** distinct inputs for tracking Mood, Stress Levels, Sleep Hours, Water Intake, and Diet.
* **Insights Dashboard:** (In Development) A dedicated space to view correlations between your habits and skin health.

## Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material Design 3)
* **Architecture:** Activity-based with Composable UI
* **Hardware Integration:** Android Sensor Manager (Light Sensor), Camera Intents
* **State Management:** MutableState & SnapshotStateList


##  Getting Started

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/getrudech/Android_Project_3111801.git](https://github.com/getrudech/Android_Project_3111801.git)
    ```
2.  **Open in Android Studio:**
    * File > Open > Select the project folder.
3.  **Build the project:**
    * Wait for Gradle sync to complete.
4.  **Run:**
    * Connect a physical device or use an Emulator (ensure it has a camera/light sensor enabled).
    * Click the "Run" button.


# Android Jetpack Compose UI Template

## Overview
This repository provides a clean, modern Android Jetpack Compose UI template. It has been strictly refactored to remove all underlying system privilege dependencies, root detection mechanisms, and legacy Java code. It serves as an out-of-the-box, 100% Kotlin-based architectural scaffold for developing modern Android user interfaces.

## Key Features

- **Pure Kotlin Architecture**: All legacy Java utility classes have been completely removed. The codebase strictly adheres to modern Kotlin conventions and Coroutines.
- **Modern UI Rendering Engine**: The entire user interface is constructed using declarative UI with Android Jetpack Compose.
- **Dynamic Theming Support**: Integrated with Material 3 and Miuix design systems, allowing for responsive dynamic color extraction and dark mode compatibility.
- **Dependency Isolation**: All backend server dependencies, superuser (su) path handlers, and module execution logic have been fully decoupled and removed. The project functions purely as a frontend display framework.
- **Localization & Sanitization**: Comprehensive `strings.xml` resources are provided in Chinese. Placeholder strings have been meticulously applied to decouple any specific business logic from the UI layer.

## Getting Started

### Prerequisites
- Android Studio Ladybug (or newer recommended)
- JDK 21 (configured within Gradle environment)

### Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Rikka06/Android-UI-Template.git
   ```

2. **Open the project:**
   Import the project directory into Android Studio. The build system is fully configured using Gradle Kotlin DSL (`build.gradle.kts`).

3. **Build the APK:**
   Execute the following command in your terminal to build a debug version of the application:
   ```bash
   ./gradlew assembleDebug
   ```
   The compiled APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## Technical Stack

- **Language**: Kotlin 
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel) utilizing StateFlow
- **Build System**: Gradle (Kotlin DSL)

## License

This project is intended as a foundational UI scaffold. You are free to modify, extend, and distribute the codebase according to your development requirements.

# ClockWidget
ClockWidget is an Android app for creating and customizing a custom clock widget. 
The app allows users to flexibly customize the widget's appearance, including layouts, backgrounds, and localization, while ensuring that background updates run smoothly.

# 📋 Features
•Flexible Customization: Select different layouts and background images for the widget. 
•Personalization: Support for custom backgrounds. 
•Multilingual: Built-in widget and app language change system (dynamic locale switching). 
•Battery optimization: Integrated power optimization status check to ensure uninterrupted time updates. 
•Modern UI: Use View Binding, transition animations, and Material Design components. 
•MVVM architecture: Separation of logic and view using ViewModel and LiveData.

# 🛠 Technology stack
•Programming Language: Java 
•Architectural Components: 
  •ViewModel - Manage Interface State Data 
  •LiveData - Reactively Update UI. 
  •View Binding - Securely Interact with Layout Views 
•Android Jetpack: 
  •AppCompat & Fragment (v1.8.9+) 
  •ConstraintLayout for Complex Interfaces 
  •ActivityResultLauncher to securely retrieve data from other Activities 
  •WorkManager (library enabled for background tasks)

•UI/UX: 
  •Custom Animations (Alpha Animations at Login). 
  • Dynamic system locale change via AppCompatDelegate.

# 📂 Project structure (main components)
•ConfigActivity.java: The main screen of the widget settings. Controls parameter selection and widget. 
•ConfigViewModel.java initialization: Configuration data processing logic, system language detection, and button state management. 
•BatteryOptimizationHelper / BatteryDialogFragment: A system for notifying the user to exclude the application from power saving modes for the correct operation of the clock. 
•ClockWidget.java: Widget provider class responsible for updating it and rendering. 
•ContainerActivity.java: Helper activity for selection of specific parameters (layouts, languages).

# 🚀 How to use
1.Install the app on your device. 
2.Add the "ClockWidget" widget to your desktop. 
3.In the configuration window (ConfigActivity) that opens: 
  •Select the desired clock layout. 
  •Customize the background (preset or custom). 
  •Select the display language of the date/time. 
  •If prompted, allow running in the background (disable battery optimization). 
4.Click the "Add" button (or "Update" if the widget already exists).

# 🔧 Requirements
•Android SDK 21+ (support for modern features up to Android 13+ via AndroidX) 
•Android Studio Iguana (or later). 
•Gradle 8.0+

Developed as part of the ClockWidget project.

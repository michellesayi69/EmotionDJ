# EmotionDJ 🎵🧠

**EmotionDJ** is an Android application that detects a user's facial expression using on-device machine learning and curates a local music playlist to match their mood. 

Built as a Computer Science portfolio project to demonstrate modern Android architecture, on-device ML integration, and audio lifecycle management.

## 📱 Core Features

*   **Real-time Emotion Detection:** Uses CameraX and Google ML Kit to analyze facial attributes (smiling probability, eye openness) via the front-facing camera.
*   **Local Mood-Based Playback:** Automatically maps detected moods to local audio tracks using `MediaPlayer`.
*   **Audio Focus Management:** Gracefully handles interruptions (pauses when a phone call or YouTube Music starts, resumes when finished).
*   **Playback Controls:** A custom Jetpack Compose UI (`MusicBar`) providing play, pause, skip, and seek functionality.
*   **Manual Spotify Hand-off:** A dedicated button pauses local music and safely hands off to the Spotify app for a specific artist.
*   **Mood History:** Stores past moods locally using Room Database.

## 🏗️ Architecture & Tech Stack

*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
*   **Machine Learning:** Google ML Kit (Face Detection)
*   **Async Programming:** Kotlin Coroutines & StateFlow
*   **Local Storage:** Room (SQLite)
*   **Audio:** Android `MediaPlayer` API, `AudioManager` (Audio Focus)
*   **Camera:** CameraX

### How the Playback Pipeline Works
1. `CameraHelper` binds CameraX and passes frames to ML Kit.
2. `MoodDetector` parses ML Kit's facial attributes and determines a mood.
3. `MainViewModel` receives the mood, updates the UI, and triggers the `MusicPlayer`.
4. `MusicPlayer` requests Audio Focus, loads the matching queue from `MoodCatalog`, and begins playback.

## 🚦 Honest Feature Status

In the spirit of engineering transparency, here is the exact status of the project's features:

| Feature | Status | Notes |
| :--- | :--- | :--- |
| Camera Mood Detection | ✅ Implemented | Uses ML Kit attributes + custom thresholds. |
| Local Music Playback | ✅ Implemented | Full queue, skip, seek, audio focus. |
| Spotify Hand-off | ✅ Implemented | Manual trigger to avoid interrupting local audio. |
| Mood History (Room) | ✅ Implemented | Saves and displays past moods. |
| Voice Input | 🟡 Partial | RecognizerIntent is wired, but end-to-end UX needs polish. |
| Statistics/Trends | ❌ Planned | DAO currently exposes only raw history. |
| Background Service | ❌ Planned | Playback lives in ViewModel; foreground service is next. |

## ⚠️ Disclaimer
EmotionDJ is an **experimental** academic project. Facial expressions are an unreliable proxy for actual emotional state. This application is **not** intended for clinical or psychological assessment. 

## 🚀 How to Build and Run

1. Clone the repository.
2. Ensure you have Android Studio and JDK 17+ installed.
3. Create a `local.properties` file in the root directory with your SDK path: `sdk.dir=/path/to/your/Android/sdk`.
4. Build via terminal: `./gradlew assembleDebug`
5. Install to a connected device/emulator: `./gradlew installDebug`

*(Note: The `res/raw/` folder currently contains 5-second sine-wave stubs for pipeline testing. Replace them with licensed audio tracks for a production build.)*

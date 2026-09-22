# EmotionDJ — AI Emotion-Based Playlist Generator

[![License: 
MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024+-brightgreen.svg)](https://developer.android.com/)
[![Jetpack 
Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg)](https://developer.android.com/jetpack/compose)

An Android application that detects a user's emotional state through 
facial expression analysis and dynamically generates a personalised music 
playlist to match or enhance that mood. All inference runs on-device, with 
no network calls required for emotion detection.

---

## Overview

EmotionDJ bridges on-device machine learning with audio playback to create 
a mood-aware listening experience. The app uses the device camera to 
capture a facial expression, runs emotion classification locally, and maps 
the detected emotion to a curated playlist. The interface is built 
entirely in Jetpack Compose and follows the MVVM architecture pattern.

The project includes offline-first design, text-to-speech narration, voice 
input, haptic feedback, and a demonstration mode for presentations.

---

## Features

- **Real-time emotion detection** — Classifies facial expressions into 
emotion categories using an on-device model.
- **Automatic playlist generation** — Maps detected emotion to a curated 
playlist.
- **Manual mood selection** — Users can override camera detection and 
choose a mood manually.
- **Room database history** — Every mood detection is timestamped and 
stored locally for the user's mood history.
- **Statistics screen** — Displays mood trends over time.
- **Text-to-speech narration** — Announces the detected mood and playlist.
- **Voice input** — Users can speak their mood as an alternative to camera 
scanning.
- **Offline mode** — Warns the user when offline and continues functioning 
with local data.
- **Demo mode** — Useful for presentations, hides sensitive UI elements 
and simulates detections.
- **Full playback controls** — Play, pause, skip, and seek within tracks.
- **Haptic feedback** — Tactile response on detection and interaction.
- **Artist personalisation** — Tracks user preferences and adjusts 
playlist recommendations.
- **Notification reminders** — Periodic mood check-ins via local 
notifications.
- **Tutorial onboarding** — First-launch walkthrough for new users.

---

## Architecture
┌────────────────────────────────────────────────────────────────┐
│ EmotionDJ Pipeline │
├────────────────────────────────────────────────────────────────┤
│ │
│ [ CameraX Preview ] │
│ ↓ │
│ [ Face Detection + Emotion Classification ] │
│ ↓ │
│ Detected emotion → happy / sad / energetic / relaxed │
│ ↓ │
│ [ Playlist Mapper ] │
│ ↓ │
│ [ MediaPlayer + Playback UI ] │
│ │
│ Side flows: │
│ • Mood history → Room database → Stats screen │
│ • TTS + Voice → accessibility layer │
│ • Notifications → periodic reminders │
│ │
└────────────────────────────────────────────────────────────────┘**Package 
structure:**

- `MainActivity.kt` — entry point
- `EmotionDJApplication.kt` — application class
- `viewmodel/` — MainViewModel, SettingsViewModel, HistoryViewModel
- `ui/screens/` — Splash, Tutorial, Main, History, Settings, Stats
- `ui/components/` — MoodBadge, PlaylistCard, OfflineWarningBanner, 
ScanningAnimation, FloatingReturnButton
- `ui/theme/` — Color, Theme, Type
- `data/` — Room database, DAO, repository, entity, type converters
- `utils/` — CameraHelper, MoodDetector, MoodHistoryManager, 
NotificationHelper, SoundPlayer, TTSHelper, VoiceHelper, HapticHelper, 
ConnectivityHelper, ArtistPreferenceManager, DemoMode, ThrottleManager

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Camera | CameraX |
| On-device detection | Custom MoodDetector module |
| Local database | Room (SQLite) |
| Audio playback | Android MediaPlayer |
| Text-to-speech | Android TTS API |
| Voice input | Android SpeechRecognizer |
| Architecture | MVVM + Repository |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle (Kotlin DSL) |

---

## Setup Instructions

### Prerequisites

- Android Studio (latest version)
- JDK 17 or higher
- Android device or emulator running API 24 or higher

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/michellesayi69/EmotionDJ.git
   cd EmotionDJ
Open the project in Android Studio.
Sync Gradle (Android Studio will prompt you).
Connect a device or start an emulator.
Click Run in Android Studio.
Grant camera permission when prompted — this is required for facial 
emotion detection.
If the app is offline, the Offline Warning Banner appears and a demo mode 
is available.
Known Limitations

Emotion detection accuracy depends on lighting, camera angle, and face 
visibility. Best results in well-lit environments with a clear frontal 
view.
The classifier recognises a limited set of emotion categories. It is not 
intended for clinical or psychological assessment.
Audio files must be provided by the user and placed in 
app/src/main/res/raw/ — the app does not stream from external music 
services.
Voice input requires an active network connection for speech recognition 
in some Android versions.
Future Improvements

Streaming service integration (Spotify, Apple Music) via public APIs
Multi-face detection for group mood analysis
Personalised mood-to-playlist mapping learned over time
Background playback with a foreground service
Wear OS companion app
License

This project is licensed under the MIT License — see the LICENSE file for 
details.

Author

Michelle Sayi — GitHub

This project was originally developed as an academic submission (2026).

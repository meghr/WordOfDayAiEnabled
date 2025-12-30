# VocabDaily - Word of the Day Android App

VocabDaily is a modern Android application built with Kotlin and Jetpack Compose that helps users learn a new English word every day. The app leverages Google's Gemini AI to fetch unique, sophisticated words, complete with meanings, examples, and translations in Hindi and Marathi.

## ✨ Features

- **Word of the Day**: Fetches a new, unique word from the Gemini AI.
- **Detailed Information**: Provides definitions, synonyms, antonyms, and example sentences.
- **Bilingual Translations**: Includes meanings and example sentences in both Hindi and Marathi.
- **Audio Pronunciation**: Integrated Text-to-Speech (TTS) to listen to the pronunciation of words and sentences.
- **History**: Automatically saves previously learned words for review.
- **Daily Notifications**: Users can schedule a daily notification to receive the word of the day at a specific time.
- **API Quota Management**: Smartly limits API calls to 30 per day and stops background tasks if the quota is exceeded.
- **Share Functionality**: Share the full word details with friends via any text-based app.
- **Modern UI**: Clean, intuitive, and responsive UI built with Jetpack Compose and Material 3.

## 🛠️ Tech Stack & Architecture

- **UI**: Jetpack Compose, Material 3
- **Architecture**: Clean Architecture (UI, Domain, Data layers)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Dependency Injection**: Hilt
- **Database**: Room for local storage
- **AI**: Google Gemini Pro for generative content
- **Background Tasks**: WorkManager for scheduling daily notifications
- **Data Storage**: DataStore for user preferences (daily limits, etc.)
- **JSON Parsing**: Kotlinx Serialization

## 🚀 Setup & Build

1. Clone the repository.
2. Add your Google Gemini API Key to the `local.properties` file:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```
3. Open the project in Android Studio.
4. Sync Gradle and build the project.


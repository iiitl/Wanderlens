# WanderLens

WanderLens is a feature-rich Android application designed for travelers to document and enrich their journeys. It seamlessly integrates powerful text generation for journaling and robust cloud storage for managing your memories.

## Features

*   **Smart Journaling**: Powered by the Gemini API, get intelligent suggestions and enhancements for your travel entries.
*   **Cloud Media Storage**: Effortlessly upload and manage your travel photos using Cloudinary integration.
*   **Secure Authentication & Database**: Built with Firebase Authentication for secure sign-ins and Cloud Firestore for robust, real-time data storage.

## Tech Stack

*   **Language**: Kotlin
*   **Architecture Components**: Navigation Component, ViewModel, LiveData
*   **Networking**: Retrofit2, OkHttp3
*   **Backend & DB**: Firebase (Auth, Firestore, Storage)
*   **Media**: Glide, Cloudinary API
*   **AI Integration**: Google Gemini API

## Prerequisites

Before you begin, ensure you have the following requirements met:

*   **Android Studio**: The latest minimum version required to run SDK 36.
*   **Firebase Account**: A Firebase project configured for Android (you'll need the `google-services.json` file).
*   **Gemini API Key**: An API key from Google AI Studio.
*   **Cloudinary Account**: A Cloudinary account for photo and media management containing your Cloud Name, API Key, and API Secret.

## Setup Guide

Follow these steps to get your development environment set up:

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/WanderLens.git
cd WanderLens
```

### 2. Configure Firebase

1.  Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
2.  Add an Android app to your project with the package name `com.example.wanderlens`.
3.  Download the `google-services.json` file provided by Firebase.
4.  Place the `google-services.json` file inside the `app/` directory of your cloned repository.

### 3. Setup Environment Variables (API Keys)

For security reasons, API keys are not version-controlled. You must provide them locally to compile the project successfully.

1.  Open the `local.properties` file located in the root directory of the project. If it doesn't exist, create it.
2.  Add your API keys to the file in the following format:

```properties
# local.properties

# Google Gemini API
GEMINI_API_KEY=your_gemini_api_key_here

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name_here
CLOUDINARY_API_KEY=your_cloudinary_api_key_here
CLOUDINARY_API_SECRET=your_cloudinary_api_secret_here
```

> **Security Warning**: *Never* commit your `local.properties` file to Git. It is already included in the `.gitignore` setup to keep your project credentials secure.

### 4. Build and Run

1.  Open the project in Android Studio.
2.  Click on **Sync Project with Gradle Files** to ensure all dependencies and the `BuildConfig` variables are loaded properly.
3.  Select an emulator or connect a physical device.
4.  Click the **Run** button (`Shift + F10`) to build and launch WanderLens.

## License

This project is licensed under the [MIT License](LICENSE).

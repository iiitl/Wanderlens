# Contributing to WanderLens 🌍📸

Hello, fellow traveler and developer! Thank you for your interest in contributing to **WanderLens**. Whether it's adding exciting new travel journaling features, refining the UI, fixing bugs, or improving our Cloudinary/Gemini backend logic, we are thrilled to have your help!

## 📝 Table of Contents
1. [Getting Started](#getting-started)
2. [Project Architecture & Tech Stack](#project-architecture--tech-stack)
3. [Environment Setup](#environment-setup)
4. [Development Workflow](#development-workflow)
5. [Pull Request Guidelines](#pull-request-guidelines)

---

## 🚀 Getting Started

1. **Fork the repository**: Click the "Fork" button at the top right of this page.
2. **Clone your fork**:
   ```bash
   git clone https://github.com/<your-username>/WanderLens.git
   ```
3. **Open the project**: Open Android Studio and select **Open existing Android Studio project**, then point it to the WanderLens folder.

## 🏗️ Project Architecture & Tech Stack

WanderLens applies modern Android development practices. Here's what you need to know before touching the code:
- **Language**: 100% Kotlin.
- **UI & Navigation**: XML layouts with **ViewBinding**, utilizing the **Android Jetpack Navigation Component** (SafeArgs included).
- **Architecture**: MVVM (Model-View-ViewModel). 
- **Networking/Media**: Retrofit for API calls and Glide for image rendering.
- **Backend & AI**: Google Firebase (Auth, Firestore, Storage) paired with Cloudinary for fast image processing and Gemini API for smart journaling features!

## 🔐 Environment Setup (Crucial!)

### 1. API Keys (`local.properties`)
This project uses secure API keys that shouldn't be pushed to GitHub. To build the project successfully, you **must** create a `local.properties` file in the root of the project with your own keys (or dummy values just to compile):

```properties
GEMINI_API_KEY=your_gemini_api_key_here
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

### 2. Firebase Project Setup
To run the app locally, you must create your own free Firebase environment. The `app/google-services.json` file is deliberately ignored in `.gitignore` to protect production Firebase credentials. **The app will crash during the Gradle build if this file is missing.**

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project and add an Android app with the package name `com.example.wanderlens`.
3. Go to **Authentication -> Sign-in method** and enable **Email/Password** and **Google** as sign-in providers.
4. In your Firebase Project Settings, add your local **SHA-256** (and SHA-1) certificate fingerprint to your Android App configuration (required for Google Sign-In).

### 3. Firestore Database Initialization
Next, you need to set up Firestore for the app to store data:
1. Go to **Firestore Database** and click **Create database**.
2. Select the **nearest location/zone** to your region.
3. Start in **Test Mode** (this allows initial reads/writes while you develop).
4. For better security, once the database is created, go to the **Rules** tab and enforce authentication by setting the following rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 4. Download `google-services.json` 
Enabling products like Firestore can update the backend configuration that needs to be bundled inside your app. 
1. **After completing all the setup steps above (Auth, SHA fingerprints, and Firestore)**, go to your Firebase Project Settings.
2. Download the updated `google-services.json` file.
3. Place it in the `app/` directory.

## 💻 Development Workflow

1. **Create a branch**: Always branch off from the main branch. 
   ```bash
   git checkout -b feature/awesome-new-journal-view
   # or
   git checkout -b fix/auth-crash
   ```
2. **Do your magic**: Write code following standard Kotlin conventions. Ensure you use `ViewBinding` in Fragments and keep your business logic inside the `ViewModel` (e.g., `AuthViewModel.kt`).
3. **Test your code**: If your changes impact Firebase or Cloudinary, test the flow locally on an Android Emulator or physical device. 
4. **Commit format**: Write a concise commit message detailing *what* and *why*:
   ```bash
   git commit -m "Fix memory leak in JournalDetailFragment image loading"
   ```

## 📬 Pull Request Guidelines

Ready to merge? Follow these rules:
- **Link the issue**: Include `Fixes #12` or `Resolves #34` in your PR description.
- **Keep it focused**: Do not mix multiple unrelated features into one PR.
- **Attach Screenshots**: If you changed any `fragment_*.xml` UI elements, you **must** attach a "Before" and "After" screenshot. We love beautiful UI, but we need to see it to review it!
- **Feedback**: A maintainer will review your code. Be open to feedback and tweak your branch if requested.

## 🆘 Need Help?
If you get stuck on setting up Firebase, encounter strange build errors, or just need guidance on the architecture, please don't hesitate to reach out! Drop a comment on your assigned issue and ping me (the primary maintainer). I am more than happy to help you navigate through any roadblocks!

### Happy Coding and Keep Wandering! 🏕️

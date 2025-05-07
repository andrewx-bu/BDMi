# BDMi
**IMDb but backwards.**  
A modern Android app for discovering and reviewing movies with added social features utilizing. Built with Kotlin, Jetpack Compose, TMDB API, and Firebase.

---

## Tech Stack

- **Kotlin, Jetpack Compose, Android Studio**
- **[TMDB API](https://developer.themoviedb.org/docs/getting-started)** – Fetches movie data and metadata.
- **Firebase Firestore** – NoSQL database for real-time user and app data management.
- **Firebase Authentication** - Handles secure user login and account management.
- **Hilt Framework** – Simplifies dependency injection in Android development.
- **[Cloudinary](https://cloudinary.com/)** – Used for storing images like profile pictures and movie posters.

---

## Local Development Setup

To get started with running this app locally, follow these steps:

### Setup Instructions

1. **Firebase Setup**  
   - Download the `google-services.json` file from your Firebase project (make sure the Android app is added with package name `com.example.bdmi`).
   - Enable Firebase Authentication and enable Email/Password sign in option
   - Place the `google-services.json` file inside your project's `app/` directory (the file should be added to .gitignore).

2. **API Keys**
   - Acquire an API key from [TMDB](https://developer.themoviedb.org/docs/getting-started).
   - Create a Cloudinary account from [here](https://cloudinary.com/console).

3. **Secrets Configuration**
   - Add the following to your `local.properties` file (the file should be added to .gitignore):

   ```properties
   TMDB_API_KEY=your_tmdb_key

   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_cloudinary_key
   CLOUDINARY_API_SECRET=your_cloudinary_secret
   ```
---
## Features

- Browse trending, popular, and genre-specific movies
- Search for movies and other users
- Write reviews with star ratings and spoiler tags
- View detailed movie information including cast, crew, and studios
- Add and manage friends with a mutual friendship system
- Access friends’ reviews and public watchlists
- Create and organize custom watchlists (public or private)
- Responsive UI optimized for both phones and tablets

---
## Project Structure
```
app/
├── data/               # Models, repositories, and Firestore integration
│   ├── api/models/     # API models (JSON responses to Kotlin data classes)
│   ├── repositories/   # Abstraction layer for Firestore and API interactions
|   |                     Also contains all data classes/objects
|   ├── utils/          # Singleton modules for dependency injection (di), and other misc. objects and functions
│
├── navigation/         # Navigation graphs and route definitions
|
├── ui/                 # Jetpack Compose UI components
|   ├── composables/... # Screen specific UI elements
│   ├── profile/        # Profile screens and corresponding Hilt View Models
│   ├── theme/          # Colors, typography, UI constants, UI dimensions 
|   ├── .../            # Remaining other screen composables and corresponding view models
|
└── MainActivity.kt     # App entry point and root navigation host
```



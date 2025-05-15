# BDMi
**IMDb backwards.**  
A modern Android app for discovering and reviewing movies with added social features. 
Built with Kotlin, Jetpack Compose, TMDB API, and Firebase.

---
## Features

- Browse trending, popular, upcoming, or top-rated movies
- Explore by genre, actor, country, or production studio
- Filter by rating, vote count, release date, and more
- Search for movies and other users
- Write reviews with star ratings and spoiler tags
- View detailed movie information including cast, crew, and studios
- Add and manage friends with a mutual friendship system
- Access friends’ reviews and public watchlists
- Create and organize custom watchlists (public or private)
- Responsive UI optimized for both phones and tablets
---
## Tech Stack

- **Kotlin, Jetpack Compose**
- **Hilt** – Dependency injection framework for clean architecture.
- **[TMDB API](https://developer.themoviedb.org/docs/getting-started)** – Source of movie data.
- **Retrofit** – Type-safe HTTP client.
- **Moshi** – JSON parser integrated with Retrofit.
- **OkHttp** – Underlying network client for Retrofit with logging and interceptors.
- **Firebase Firestore** – NoSQL cloud database for real-time data sync.
- **Firebase Authentication** – Secure user login and session management.
- **Coil** – Fast image loading library optimized for Compose.
- **[Cloudinary](https://cloudinary.com/)** – Cloud storage for user images and movie posters.
---
## Local Development Setup

To get started with running this app locally, follow these steps:

### Setup Instructions

1. **Firebase Setup**  
   - Download the `google-services.json` file from your Firebase project (make sure the Android app is added with package name `com.example.bdmi`).
   - Enable Firebase Authentication and enable Email/Password sign in option.
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
## Project Structure
```
app/
├── data/               # Models, repositories, and Firestore integration
│   ├── api/models/     # API models (JSON responses to Kotlin data classes)
│   ├── repositories/   # Abstraction layer for Firestore and API interactions
|   |                     Also contains all data classes/objects
|   ├── utils/          # Singleton modules for dependency injection, and other misc. objects and functions
│
├── navigation/         # Navigation graphs and route definitions
|
├── ui/                 # Jetpack Compose UI components
|   ├── composables/... # Screen specific UI elements
│   ├── profile/        # Profile screens and corresponding Hilt View Models
│   ├── theme/          # Colors, Typography, UI constants, UI dimensions
|   ├── .../            # Remaining other screen composables and corresponding view models
|
└── MainActivity.kt     # App entry point and root navigation host
```
## Navigation
```
RootNavGraph
|
├── "onboarding" ─── "start"
│                       ├── "login" ────── "root"
│                       ├── "register" ─── "root"
├── "root"
     ├── "notification"
     │       ├── "user_profile/{userId}"
     │
     ├── "search"
     │       ├── "movie_detail/{movieId}"
     │
     ├── "friend_search"
     │       ├── "user_profile/{userId}"
     │
     ├── "watchlists"
     │       ├── "watchlists/{userId}" ── "watchlist/{userId}/{listId}" 
     │                  ├── "movie_detail/{movieId}"
     ├── "user_profile/{userId}"
     │       ├── "watchlists/{userId}"
     │       ├── "user_reviews/{userId}"
     │       ├── "friends/{userId}"
     │       ├── "movie_detail/{movieId}"
     │
     ├── "home"
             ├── "movie_detail/{movieId}"
                        ├── "user_profile/{userId}"
                        ├── "reviews/{movieId}" ────── "user_profile/{userId}"
                        ├── "person/{personId}" ────── "movie_detail/{movieId}"
                        ├── "genre/{genreId}" ──────── "movie_detail/{movieId}"
                        ├── "studio/{studioId}" ────── "movie_detail/{movieId}"
                        ├── "country/{countryCode}" ── "movie_detail/{movieId}"
```

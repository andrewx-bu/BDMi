# BDMi
**IMDb but backwards.**  
A modern Android app for discovering and exploring movies with added social features.

---

## Tech Stack

- **[TMDB API](https://developer.themoviedb.org/docs/getting-started)** – Fetches movie data and metadata.
- **Firebase Firestore** – NoSQL database for real-time user and app data management.
- **Hilt** – Simplifies dependency injection in Android development.
- **[Cloudinary](https://cloudinary.com/)** – Used for storing images like profile pictures and movie posters.

---

## Local Development Setup

To get started with running this app locally, follow these steps:

### Setup Instructions

1. **Firebase Setup**  
   - Download the `google-services.json` file from your Firebase project (make sure the Android app is added with package name `com.example.bdmi`).
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
## Sprint 1 Progress

### ✅ Completed Features

- **UI Skeleton and Navigation**
  - Initial layout structure for the app using Jetpack Compose.
  - Used the Compose Navigation library for easy navigation.
  
- **Basic Onboarding Flow**
  - Login and Registration pages implemented.
  - Navigation between onboarding screens.

- **Database Integration**
  - Connected Firebase Firestore for user management.
  - Basic user registration and authentication working.

- **Movie API Integration**
  - Basic API call to TMDB to retrieve a list of movies.
  - Movie titles displayed in a basic UI component.
    
- **Login Persistence**
  - Remember logged-in user after app close using SharedPreferences or an alternative method.

 - **Cloudinary Integration**
   - Upload and save user profile pictures using Cloudinary.
   - Store image URLs in Firestore.
---

### 🔧 Work in Progress
- **Expanded TMDB API Usage**
  - Fetch movie details, trailers, and genres.
  - Add search functionality and filtering options.

---



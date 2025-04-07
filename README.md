# BDMi
IMDb but backwards

## Tech Stack
- **TMDB**: Used for fetching movie data and details.
- **Firebase Firestore**: A NoSQL database for managing app data in real-time.
- **Hilt**: Dependency injection for simplifying app development.
- **Cloudinary**: Used for storing images such as profile pictures and movie posters

# Local Development Setup
To get started with running this app locally, follow these steps:

## Setup Instructions
1. Obtain the `google-services.json` file from your Firebase project (ensuring the app is added with path com.example.bdmi).
2. Place the `google-services.json` file in the `app/` directory of your project. This is required to initialize the Firebase Firestore database.
3. Aquire an API key from [TMDB](https://developer.themoviedb.org/docs/getting-started) and create a [Cloudinary](https://console.cloudinary.com/console/c-5b1aacb9f6c0fb3b66cb2372c16289/home/product-explorer) account. Placing the below keys and secrets in the local.properties file.

```kt
TMDB_API_KEY=[KEY]

CLOUDINARY_CLOUD_NAME=[CLOUD-NAME]
CLOUDINARY_API_KEY=[KEY]
CLOUDINARY_API_SECRET=[SECRET]
```
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

---

### 🔧 Work in Progress

- **Cloudinary Integration**
  - Upload and save user profile pictures using Cloudinary.
  - Store image URLs in Firestore.

- **Expanded TMDB API Usage**
  - Fetch movie details, trailers, and genres.
  - Add search functionality and filtering options.

- **Login Persistence**
  - Remember logged-in user after app close using SharedPreferences or an alternative method.

---



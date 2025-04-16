package com.example.bdmi.data.utils

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
* Provides a singleton instance of FirebaseFirestore for dependency injection
* Module setup for Hilt for DI created by Gemini AI
 */

@Module
@InstallIn(SingletonComponent::class) //Singleton scope for app-wide use
object FirestoreModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore //Initializes Firestore instance
    }
}

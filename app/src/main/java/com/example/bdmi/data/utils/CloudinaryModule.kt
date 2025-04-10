package com.example.bdmi.data.utils

import com.cloudinary.android.MediaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Provides a singleton instance of Cloudinary for dependency injection
@Module
@InstallIn(SingletonComponent::class)
object CloudinaryModule {
    @Provides
    @Singleton
    fun provideCloudinary(): MediaManager = MediaManager.get()
}

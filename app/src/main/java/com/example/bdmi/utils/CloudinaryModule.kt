package com.example.bdmi.utils

import com.example.bdmi.BuildConfig
import com.cloudinary.Cloudinary
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
    fun provideCloudinary(): Cloudinary {
        val config = HashMap<String, String>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET

        return Cloudinary("cloudinary://${config["api_key"]}:${config["api_secret"]}@${config["cloud_name"]}")
    }
}

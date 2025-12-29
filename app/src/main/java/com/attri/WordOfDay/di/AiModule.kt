package com.attri.WordOfDay.di

import com.attri.WordOfDay.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        // Based on your curl response, "gemini-2.0-flash" is available and supports "generateContent".
        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey
        )
    }
}

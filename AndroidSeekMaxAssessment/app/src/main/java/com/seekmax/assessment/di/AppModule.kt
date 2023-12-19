package com.seekmax.assessment.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.seekmax.assessment.APP_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPrefsEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }
}
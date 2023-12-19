package com.seekmax.assessment.di

import android.content.SharedPreferences
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.google.gson.Gson
import com.seekmax.assessment.USER_TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(preferences: SharedPreferences): OkHttpClient {

        val authInterceptor = AuthInterceptor(preferences)
        return if (/*BuildConfig.DEBUG*/ true) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        } else {
            OkHttpClient
                .Builder()
                .addInterceptor(authInterceptor)
                .build()
        }
    }

    @Singleton
    @Provides
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("http://172.22.240.1:3002")
            // .serverUrl("http://192.168.1.6:3002")
            .okHttpClient(okHttpClient)
            .build()
    }
}

class AuthInterceptor(private val preferences: SharedPreferences) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var token = preferences.getString(USER_TOKEN, "")
        token = if (token.isNullOrEmpty()) "" else "Bearer $token"

        Log.d("token", token)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token).build()

        return chain.proceed(request)
    }
}

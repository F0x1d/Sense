package com.f0x1d.sense.di

import com.f0x1d.sense.repository.network.service.OpenAIService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideOpenAIService(client: OkHttpClient, gson: Gson) = Retrofit.Builder()
        .baseUrl("https://google.com/") // it is always modified in ChangeEndpointInterceptor.kt
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(OpenAIService::class.java)
}
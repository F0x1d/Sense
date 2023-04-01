package com.f0x1d.sense.di

import com.f0x1d.sense.repository.network.service.AuthenticationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticationInterceptor: AuthenticationInterceptor) = OkHttpClient.Builder()
        .addInterceptor(authenticationInterceptor)
        .readTimeout(2, TimeUnit.MINUTES)
        .build()
}
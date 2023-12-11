package com.androidstrike.expos.onlinetictactoe.di

import com.androidstrike.expos.onlinetictactoe.data.KtorRealtimeMessagingClient
import com.androidstrike.expos.onlinetictactoe.data.RealtimeMessagingClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module()
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRealtimeMessagingClient(httpClient: HttpClient): RealtimeMessagingClient{
        return  KtorRealtimeMessagingClient(httpClient)
    }
}
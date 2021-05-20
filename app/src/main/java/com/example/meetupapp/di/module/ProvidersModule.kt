package com.example.meetupapp.di.module

import android.content.Context
import com.example.meetupapp.util.providers.IStringProvider
import com.example.meetupapp.util.providers.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvidersModule {

    @Singleton
    @Provides
    fun provideStringProvider(
        @ApplicationContext context: Context
    ): IStringProvider {
        return StringProvider(context)
    }
}
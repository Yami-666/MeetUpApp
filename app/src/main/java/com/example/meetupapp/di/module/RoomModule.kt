package com.example.meetupapp.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
//    @Singleton
//    @Provides
//    fun providePostDatabase(@ApplicationContext context: Context): PostDatabase {
//        return Room.databaseBuilder(
//            context,
//            PostDatabase::class.java,
//            DATABASE_NAME)
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    fun providePostDao(postDatabase: PostDatabase): IPostDao {
//        return postDatabase.postDao()
//    }
}
package com.example.meetupapp.source.remote

import com.example.meetupapp.source.remote.retrofit.IPostRetrofit
import javax.inject.Inject

class PostsRemoteDataSource @Inject constructor(
    private val retrofit: IPostRetrofit
) : IPostsRemoteDataSource {
    
}

interface IPostsRemoteDataSource {
}

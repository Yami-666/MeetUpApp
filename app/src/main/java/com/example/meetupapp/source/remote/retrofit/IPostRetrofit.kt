package com.example.meetupapp.source.remote.retrofit

import com.example.meetupapp.pojo.ContactUi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IPostRetrofit {
    @GET("api/get_users")
    fun getUsers(@Path("id") id: Int): Call<ContactUi>
}
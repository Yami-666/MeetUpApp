package com.example.meetupapp.util

import java.lang.Exception

sealed class DataState<out R> {
    data class Success<out T>(val data: T): DataState<T>()
    data class Error(val errorText: Exception) : DataState<Nothing>()
    object Loading: DataState<Nothing>()

    fun <T> Success<T>.getData() : T {
        return data
    }
}


package com.example.meetupapp.domain

interface IUseCase {
    interface In<T> {
        suspend fun invoke(param: T)
    }

    interface Out<T> {
        suspend fun invoke(): T
    }

    interface InOut<T, V> {
        suspend fun invoke(param: T): V
    }
}

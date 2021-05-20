package com.example.meetup.extensions

fun Int?.orZero(): Int {
    return this ?: 0
}
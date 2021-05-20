package com.example.meetupapp.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRegistration(
    val id: String,
    val phoneNumber: String,
) : Parcelable

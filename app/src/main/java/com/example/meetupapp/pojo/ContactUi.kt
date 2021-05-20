package com.example.meetupapp.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactUi(
    var id: String = "",
    val phone: String = "",
    val nickname: String = "",
    val biography: String = "",
    val fullname: String = "",
    val status: String = "",
    val photoUrl: String = "",
) : Parcelable
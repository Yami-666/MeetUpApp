package com.example.meetupapp.pojo.enum

import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_STATE
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_USERS

enum class UserStatus(val status: String) {
    ONLINE("online"),
    OFFLINE("offline"),
    TYPING("typing");

    companion object {
        fun updateUserStatus(userStatus: UserStatus) {
            CURRENT_UID?.let {
                FirebaseProvider.referenceDatabase
                    .child(NODE_USERS)
                    .child(it)
                    .child(CHILD_STATE)
                    .setValue(userStatus.status)
            }
        }
    }
}
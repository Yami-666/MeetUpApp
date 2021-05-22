package com.example.meetupapp.util.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseProvider {
    const val NODE_USERS = "users"
    const val NODE_PHONES = "phones"
    const val NODE_PHONE_CONTACTS = "phone_contacts"
    const val NODE_MESSAGES = "messages"

    const val CHILD_ID = "id"
    const val CHILD_PHONE = "phone"
    const val CHILD_NICKNAME = "nickname"
    const val CHILD_NAME = "name"
    const val CHILD_DATE = "date"
    const val CHILD_TIME = "time"
    const val CHILD_ADDRESS = "address"
    const val CHILD_STATUS = "status"
    const val CHILD_STATE = "state"

    // Message
    const val CHILD_TEXT = "text"
    const val CHILD_MEETING = "meeting"
    const val CHILD_TYPE = "type"
    const val CHILD_FROM = "from"
    const val CHILD_TO = "to"
    const val CHILD_TIMESTAMP = "timestamp"

    // Type message
    const val TYPE_TEXT = "text"

    val authFirebase by lazy { FirebaseAuth.getInstance() }
    val referenceDatabase by lazy { FirebaseDatabase.getInstance().reference }

    var CURRENT_UID: String? = FirebaseAuth.getInstance().currentUser?.uid
}

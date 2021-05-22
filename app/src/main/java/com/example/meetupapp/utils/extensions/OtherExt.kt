package com.example.meetupapp.utils.extensions

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.meetupapp.pojo.MeetingParams
import com.example.meetupapp.utils.Constants.EMPTY_STRING
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_ADDRESS
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_DATE
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_FROM
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_NAME
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_STATUS
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TEXT
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TIME
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TIMESTAMP
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TO
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TYPE
import com.example.meetupapp.utils.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MEETINGS
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MESSAGES
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

inline fun <reified T, reified R> R.unsafeLazy(noinline init: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE, init)
}

inline fun <reified T> T?.orIfNull(input: () -> T): T {
    return this ?: input()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun ProgressBar.show() {
    this.visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    this.visibility = View.GONE
}

inline fun <reified T> DataSnapshot.getDataModel(): T {
    return this.getValue(T::class.java) ?: T::class.java.newInstance()
}

fun View.setVisibility() {
    this.visibility = View.VISIBLE
}

fun View.setHide() {
    this.visibility = View.GONE
}

fun String.toTimeHHmmFormat(): String {
    return try {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        timeFormat.format(time)
    } catch (e: Exception) {
        Log.e("toTimeHHmmFormat", "toTimeHHmmFormat(): ${e.message}")
        EMPTY_STRING
    }
}

fun sendMessage(
    message: String,
    receivingUserId: String,
    typeMessage: String,
    doAfterCompleteSend: () -> Unit
) {
    CURRENT_UID?.let { currentUserId ->
        val refMessages = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
        val refMessagesReceiver = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"

        val messageKey = FirebaseProvider.referenceDatabase.child(refMessages).push().key
        val mapMessage = HashMap<String, Any>()

        mapMessage[CHILD_FROM] = currentUserId
        mapMessage[CHILD_TO] = receivingUserId
        mapMessage[CHILD_TYPE] = typeMessage
        mapMessage[CHILD_TEXT] = message
        mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

        val mapDialog = HashMap<String, Any>()
        mapDialog["$refMessages/$messageKey"] = mapMessage
        mapDialog["$refMessagesReceiver/$messageKey"] = mapMessage

        FirebaseProvider.referenceDatabase
            .updateChildren(mapDialog)
            .addOnCompleteListener { doAfterCompleteSend() }
    }
}


fun sendMeetingMessage(
    meetingMessage: MeetingParams,
    receivingUserId: String,
    doAfterCompleteSend: () -> Unit
) {
    CURRENT_UID?.let { currentUserId ->
        sendToNodeMessages(receivingUserId, meetingMessage, currentUserId)
        sendToNodeMeetings(receivingUserId, meetingMessage, currentUserId, doAfterCompleteSend)
    }
}

fun sendToNodeMeetings(
    receivingUserId: String,
    meetingMessage: MeetingParams,
    currentUserId: String,
    doAfterCompleteSend: () -> Unit
) {
    val refMeetings = "$NODE_MEETINGS/$CURRENT_UID/"
    val refMeetingsReceiver = "$NODE_MEETINGS/$receivingUserId/"

    val messageKey = FirebaseProvider.referenceDatabase.child(refMeetings).push().key
    val mapMessage = HashMap<String, Any>()

    mapMessage[CHILD_NAME] = meetingMessage.name
    mapMessage[CHILD_DATE] = meetingMessage.date
    mapMessage[CHILD_TIME] = meetingMessage.time
    mapMessage[CHILD_ADDRESS] = meetingMessage.address
    mapMessage[CHILD_FROM] = currentUserId
    mapMessage[CHILD_TO] = receivingUserId
    mapMessage[CHILD_STATUS] = meetingMessage.status

    val mapDialog = HashMap<String, Any>()
    mapDialog["$refMeetings/$messageKey"] = mapMessage
    mapDialog["$refMeetingsReceiver/$messageKey"] = mapMessage

    FirebaseProvider.referenceDatabase
        .updateChildren(mapDialog)
        .addOnCompleteListener {
            doAfterCompleteSend()
        }
}

private fun sendToNodeMessages(
    receivingUserId: String,
    meetingMessage: MeetingParams,
    currentUserId: String
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"

    val messageKey = FirebaseProvider.referenceDatabase.child(refDialogUser).push().key
    val mapMessage = HashMap<String, Any>()

    mapMessage[CHILD_NAME] = meetingMessage.name
    mapMessage[CHILD_DATE] = meetingMessage.date
    mapMessage[CHILD_TIME] = meetingMessage.time
    mapMessage[CHILD_ADDRESS] = meetingMessage.address
    mapMessage[CHILD_FROM] = currentUserId
    mapMessage[CHILD_TO] = receivingUserId
    mapMessage[CHILD_STATUS] = meetingMessage.status

    val mapDialog = HashMap<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    FirebaseProvider.referenceDatabase
        .updateChildren(mapDialog)
}

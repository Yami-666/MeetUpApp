package com.example.meetupapp.util.extensions

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_FROM
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_TEXT
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_TIMESTAMP
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_TO
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_TYPE
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_MESSAGES
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
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun sendMessage(
    message: String,
    receivingUserId: String,
    typeText: String,
    doAfterCompleteSend: () -> Unit
) {
    CURRENT_UID?.let { currentUserId ->
        val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
        val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"

        val messageKey = FirebaseProvider.referenceDatabase.child(refDialogUser).push().key
        val mapMessage = HashMap<String, Any>()

        mapMessage[CHILD_FROM] = currentUserId
        mapMessage[CHILD_TO] = receivingUserId
        mapMessage[CHILD_TYPE] = typeText
        mapMessage[CHILD_TEXT] = message
        mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

        val mapDialog = HashMap<String, Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

        FirebaseProvider.referenceDatabase
            .updateChildren(mapDialog)
            .addOnCompleteListener { doAfterCompleteSend() }
    }.orIfNull {

    }
}
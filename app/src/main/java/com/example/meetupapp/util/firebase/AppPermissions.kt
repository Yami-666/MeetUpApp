package com.example.meetupapp.util.firebase

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_ID
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_PHONE_CONTACTS
import com.example.meetupapp.util.firebase.FirebaseProvider.referenceDatabase

object AppPermissions {
    const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
    private const val PERMISSION_REQUEST = 200

    fun checkPermission(permission: String, activity: AppCompatActivity): Boolean {
        val permissionNotGranted = ContextCompat
            .checkSelfPermission(activity.applicationContext, permission) != PackageManager.PERMISSION_GRANTED
        return if (Build.VERSION.SDK_INT >= 23 && permissionNotGranted) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), PERMISSION_REQUEST)
            false
        } else {
            getUserContacts(activity)
            true
        }
    }

    private fun getUserContacts(activity: AppCompatActivity) {
        val userContacts = arrayListOf<ContactUi>()
        val cursor = activity.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null,
        )
        cursor?.let { cursorValue ->
            val contactName = ContactsContract.Contacts.DISPLAY_NAME
            val columnIndexName = cursorValue.getColumnIndex(contactName)
            val contactPhoneNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
            val columnIndexPhoneNumber = cursorValue.getColumnIndex(contactPhoneNumber)

            while (cursorValue.moveToNext()) {
                val fullname = cursorValue.getString(columnIndexName)
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneNumber = cursorValue.getString(columnIndexPhoneNumber)
                    .replace(Regex("[\\s,-]"), "")
                val newContact = ContactUi(
                    id = id,
                    fullname = fullname,
                    phone = phoneNumber
                )
                userContacts.add(newContact)
            }
            updatePhoneToDatabase(userContacts)
            cursor.close()
        }
    }

    private fun updatePhoneToDatabase(userContactUis: ArrayList<ContactUi>) {
        CURRENT_UID?.let {
            val request = AppValueEventListener { dataSnapshot ->
                dataSnapshot.children.forEach { snapshot ->
                    userContactUis.forEach { contact ->
                        if (snapshot.key == contact.phone) {
                            referenceDatabase.child(NODE_PHONE_CONTACTS)
                                .child(it)
                                .child(snapshot.value.toString())
                                .child(CHILD_ID)
                                .setValue(snapshot.value)
                                .addOnFailureListener {
                                    Log.w("MAIN_ACTIVITY", it.message.toString())
                                }
                        }
                    }
                }
            }

            referenceDatabase.child(FirebaseProvider.NODE_PHONES)
                .addListenerForSingleValueEvent(request)
        }
    }
}

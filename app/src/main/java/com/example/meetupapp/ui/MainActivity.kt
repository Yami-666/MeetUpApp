package com.example.meetupapp.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.meetupapp.R
import com.example.meetupapp.util.enum.UserStatus
import com.example.meetupapp.util.firebase.AppPermissions.READ_CONTACTS
import com.example.meetupapp.util.firebase.AppPermissions.checkPermission
import com.example.meetupapp.util.firebase.FirebaseProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (FirebaseProvider.authFirebase.currentUser != null) {
            UserStatus.updateUserStatus(UserStatus.ONLINE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (FirebaseProvider.authFirebase.currentUser != null) {
            UserStatus.updateUserStatus(UserStatus.OFFLINE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                initContacts()
            }
        }
    }

    private fun initContacts() {
        checkPermission(READ_CONTACTS, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permission = ContextCompat.checkSelfPermission(
            this,
            READ_CONTACTS
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            initContacts()
        }
    }
}
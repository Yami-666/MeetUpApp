package com.example.meetupapp.ui.features.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentPhoneCodeBinding
import com.example.meetupapp.pojo.enum.UserStatus
import com.example.meetupapp.util.extensions.hide
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_ID
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_NICKNAME
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_PHONE
import com.example.meetupapp.util.firebase.FirebaseProvider.CHILD_STATE
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_PHONES
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_USERS
import com.example.meetupapp.util.firebase.FirebaseProvider.authFirebase
import com.example.meetupapp.util.extensions.show
import com.example.meetupapp.util.extensions.showToast
import com.google.firebase.auth.PhoneAuthProvider

class PhoneCodeFragment : Fragment() {

    private lateinit var binding: FragmentPhoneCodeBinding
    private val args: PhoneCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_phone_code, container, false)

        binding.fabEnterPhoneCode.setOnClickListener {
            binding.progressBarPhoneCode.show()
            enterCode(binding)
        }
        return binding.root
    }

    private fun enterCode(binding: FragmentPhoneCodeBinding) {
        val id = args.userInfo.id
        val code = binding.editTextPhoneCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        val authFirebase = FirebaseProvider.authFirebase
        authFirebase.signInWithCredential(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    addUserToDatabase()
                } else {
                    binding.progressBarPhoneCode.hide()
                    showToast(authTask.exception?.message.toString())
                }
            }
            .addOnFailureListener {
                binding.progressBarPhoneCode.hide()
            }
    }

    private fun addUserToDatabase() {
        CURRENT_UID = authFirebase.currentUser?.uid.toString()
        val userPhone = args.userInfo.phoneNumber
        val dateMap = mutableMapOf<String, Any>()
        dateMap[CHILD_ID] = CURRENT_UID.orEmpty()
        dateMap[CHILD_PHONE] = userPhone
        dateMap[CHILD_NICKNAME] = userPhone
        dateMap[CHILD_STATE] = UserStatus.ONLINE.status

        FirebaseProvider.referenceDatabase.child(NODE_PHONES).child(userPhone).setValue(CURRENT_UID)
            .addOnFailureListener { showToast(it.message.toString()) }
            .addOnSuccessListener {
                FirebaseProvider.referenceDatabase.child(NODE_USERS).child(CURRENT_UID.orEmpty())
                    .updateChildren(dateMap)
                    .addOnCompleteListener { databaseTask ->
                        if (databaseTask.isSuccessful) {
                            binding.progressBarPhoneCode.hide()
                            binding.root.findNavController()
                                .navigate(PhoneCodeFragmentDirections.toChatAndMeeting())
                        } else {
                            binding.progressBarPhoneCode.hide()
                            showToast(databaseTask.exception?.message.toString())
                        }
                    }
                    .addOnFailureListener {
                        binding.progressBarPhoneCode.hide()
                    }
            }

    }
}

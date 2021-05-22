package com.example.meetupapp.ui.features.registration

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.meetupapp.pojo.UserRegistration
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentPhoneRegistrationBinding
import com.example.meetupapp.utils.extensions.hide
import com.example.meetupapp.utils.extensions.setVisibility
import com.example.meetupapp.utils.extensions.showToast
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneRegistrationFragment : Fragment() {

    private lateinit var phoneCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        checkCurrentUserSignIn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPhoneRegistrationBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_phone_registration, container, false)
        phoneCallBack = getPhoneCallBack(binding)

        binding.fabRegistrationPhoneNumber.setOnClickListener {
            val phoneNumber: String = binding.editTextPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()) {
                binding.progressBar.setVisibility()
                verifyPhone(phoneNumber)
            } else {
                showErrorMessage(binding)
            }
        }

        hideErrorMessage(binding)

        return binding.root
    }

    private fun hideErrorMessage(binding: FragmentPhoneRegistrationBinding) {
        binding.editTextPhoneNumber.addTextChangedListener {
            if (binding.layoutTextPhoneNumber.isErrorEnabled) {
                binding.layoutTextPhoneNumber.isErrorEnabled = false
            }
        }
    }

    private fun verifyPhone(enteredPhone: String) {
        sendCode(enteredPhone)
    }

    private fun showErrorMessage(binding: FragmentPhoneRegistrationBinding) {
        binding.layoutTextPhoneNumber.error = "Введите номер телефона"
        binding.layoutTextPhoneNumber.isErrorEnabled = true
    }

    private fun sendCode(enteredPhone: String) {
        val options = PhoneAuthOptions.newBuilder()
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity as AppCompatActivity)
            .setPhoneNumber(enteredPhone)
            .setCallbacks(phoneCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun getPhoneCallBack(binding: FragmentPhoneRegistrationBinding) =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Successfully!")
                        } else {
                            binding.progressBar.hide()
                            showToast(task.exception?.message.toString())
                        }

                    }
                    .addOnFailureListener {
                        showToast(it.message.toString())
                        binding.progressBar.hide()
                    }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
                binding.progressBar.hide()
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                binding.progressBar.hide()
                val phoneNumber = binding.editTextPhoneNumber.text.toString()
                val userInfo = UserRegistration(
                    id = id,
                    phoneNumber = phoneNumber
                )
                val action = PhoneRegistrationFragmentDirections.toPhoneCode(userInfo)
                binding.root.findNavController().navigate(action)
            }
        }

    private fun checkCurrentUserSignIn() {
        val currentUser = FirebaseProvider.authFirebase.currentUser
        if (currentUser != null) {
            findNavController()
                .navigate(PhoneCodeFragmentDirections.toChatAndMeeting())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_bar_menu, menu)
    }

}

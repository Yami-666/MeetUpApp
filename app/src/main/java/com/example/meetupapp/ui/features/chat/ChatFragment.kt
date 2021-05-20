package com.example.meetupapp.ui.features.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatBinding
import com.example.meetupapp.pojo.MessageUi
import com.example.meetupapp.pojo.UserModel
import com.example.meetupapp.ui.recyclerViewAdapter.ChatAdapter
import com.example.meetupapp.util.extensions.getDataModel
import com.example.meetupapp.util.extensions.sendMessage
import com.example.meetupapp.util.firebase.AppValueEventListener
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_MESSAGES
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_USERS
import com.example.meetupapp.util.firebase.FirebaseProvider.TYPE_TEXT
import com.google.firebase.database.DatabaseReference

class ChatFragment : Fragment() {
    private lateinit var listenerInfoToolBar: AppValueEventListener
    private lateinit var refUser: DatabaseReference
    private lateinit var messagesListener: AppValueEventListener
    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()

    override fun onResume() {
        super.onResume()
        listenerInfoToolBar = AppValueEventListener {
            val user = it.getDataModel<UserModel>()
            setNewUserName(user)
            binding.userStatus.text = user.state
        }
        val contactId = args.contactArg.id
        refUser = FirebaseProvider.referenceDatabase
            .child(NODE_USERS)
            .child(contactId)
        refUser.addValueEventListener(listenerInfoToolBar)
    }

    private fun setNewUserName(user: UserModel) {
        if (binding.userName.text != user.nickname) {
            binding.userName.text = user.nickname
        }
    }

    override fun onPause() {
        super.onPause()
        refUser.removeEventListener(listenerInfoToolBar)
        refUser.removeEventListener(messagesListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chat, container, false)
        binding.userName.text = args.contactArg.nickname
        initTopBarBackListener()
        initRecyclerView(binding)
        initSendMessageListener()


        return binding.root
    }

    private fun initRecyclerView(binding: FragmentChatBinding) {
        CURRENT_UID?.let { currentId ->
            val recycler = binding.messagesRecyclerView
            setLayoutManager(recycler)
            val adapter = ChatAdapter()
            recycler.adapter = adapter

            val contactId = args.contactArg.id
            val refMessages = FirebaseProvider.referenceDatabase.child(NODE_MESSAGES)
                .child(currentId)
                .child(contactId)
            messagesListener = AppValueEventListener { dataSnapshot ->
                val messages = dataSnapshot.children.map { it.getDataModel<MessageUi>() }
                adapter.setList(messages)
            }
            refMessages.addValueEventListener(messagesListener)
        }
    }

    private fun setLayoutManager(recycler: RecyclerView) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager
    }

    private fun initSendMessageListener() {
        binding.sendMessage.setOnClickListener {
            val message = binding.editTextMessage.text.trim().toString()
            if (message.isNotEmpty()) {
                sendMessage(message, args.contactArg.id, TYPE_TEXT) {
                    binding.editTextMessage.text.clear()
                }
            } else {
                return@setOnClickListener
            }
        }
    }


    private fun initTopBarBackListener() {
        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}
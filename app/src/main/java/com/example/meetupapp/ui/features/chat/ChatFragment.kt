package com.example.meetupapp.ui.features.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.pojo.MeetingParams
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatBinding
import com.example.meetupapp.pojo.MessageUi
import com.example.meetupapp.pojo.UserModel
import com.example.meetupapp.ui.adapters.MessagesAdapter
import com.example.meetupapp.ui.pickers.DatePickers
import com.example.meetupapp.ui.pickers.TimePickers
import com.example.meetupapp.utils.extensions.getDataModel
import com.example.meetupapp.utils.extensions.sendMeetingMessage
import com.example.meetupapp.utils.extensions.sendMessage
import com.example.meetupapp.utils.firebase.AppValueEventListener
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.example.meetupapp.utils.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MESSAGES
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_USERS
import com.example.meetupapp.utils.firebase.FirebaseProvider.TYPE_TEXT
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var listenerInfoToolBar: AppValueEventListener
    private lateinit var refUser: DatabaseReference
    private lateinit var messagesListener: AppValueEventListener
    private lateinit var binding: FragmentChatBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chat, container, false)
        binding.userName.text = args.contactArg.nickname

        initTopBarBackListener()
        initRecyclerView()
        initSendMessageListener()
        initMeetingBottomSheet()
        initPickTime()
        initPickDate()
        initSendMeeting()
        return binding.root
    }

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

    private fun initPickTime() =
        binding.imageAddTime.setOnClickListener {
            TimePickers.initTimePickers(parentFragmentManager)
        }

    private fun initPickDate() =
        binding.imageOpenCalendar.setOnClickListener {
            DatePickers.showDatePicker(parentFragmentManager)
        }

    private fun initMeetingBottomSheet() {
        val bottomSheetContent: ConstraintLayout = binding.bottomSheetContent
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContent).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.imageCloseBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.createMeeting.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initRecyclerView() {
        CURRENT_UID?.let { currentId ->
            val recycler = binding.messagesRecyclerView
            setLayoutManager(recycler)
            val adapter = MessagesAdapter()
            recycler.adapter = adapter

            val contactId = args.contactArg.id
            val refMessages = FirebaseProvider.referenceDatabase.child(NODE_MESSAGES)
                .child(currentId)
                .child(contactId)
            messagesListener = AppValueEventListener { dataSnapshot ->
                val messages = dataSnapshot.children
                    .map { it.getDataModel<MessageUi>() }
                    .filter { it.text.isNotEmpty() && it.timestamp != 0 }
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

    private fun initSendMeeting() {
        with (binding) {
            addMeeting.setOnClickListener {
                val meetingName = textInputNameMeeting.text.toString()
                val address = textInputAddress.text.toString()
                val time = TimePickers.time
                val addedMeeting = MeetingParams(
                    name = meetingName,
                    address = address,
                    date = DatePickers.date,
                    time = time,
                    status = MEETINGS_IN_PROGRESS
                )
                sendMeetingMessage(addedMeeting, args.contactArg.id) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    private fun initTopBarBackListener() {
        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {
        private const val MEETINGS_IN_PROGRESS = "in progress"
    }
}
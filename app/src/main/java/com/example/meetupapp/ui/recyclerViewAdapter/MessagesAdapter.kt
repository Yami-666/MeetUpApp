package com.example.meetupapp.ui.recyclerViewAdapter

import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.R
import com.example.meetupapp.databinding.ItemMessageBinding
import com.example.meetupapp.pojo.MessageUi
import com.example.meetupapp.util.extensions.setHide
import com.example.meetupapp.util.extensions.setVisibility
import com.example.meetupapp.util.extensions.toTimeHHmmFormat
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

    private var messagesCache = emptyList<MessageUi>()

    inner class MyViewHolder(itemBinding: ItemMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val blockMessageSender = itemBinding.messageSender
        val messageSender = itemBinding.rightMessage
        val messageSenderTime = itemBinding.rightMessageTime

        val blockMessageReceiver = itemBinding.messageReceiver
        val messageReceiver = itemBinding.leftMessage
        val messageReceiverTime = itemBinding.leftMessageTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMessageBinding = DataBindingUtil
            .inflate(inflater, R.layout.item_message, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            fillDataViewHolder(position)
            blockMessageSender.setOnLongClickListener {
                true
            }
            blockMessageSender.setOnClickListener {
            }

        }
    }

    private fun MyViewHolder.fillDataViewHolder(position: Int) {
        if (messagesCache[position].from == CURRENT_UID) {
            initSenderMessage(position)
        } else {
            initReceiveMessage(position)
        }
    }

    private fun MyViewHolder.initReceiveMessage(
        position: Int
    ) {
        blockMessageSender.setHide()
        blockMessageReceiver.setVisibility()
        messageReceiver.text = messagesCache[position].text

        val timeStamp = messagesCache[position].timestamp.toString()
        if (timeStamp.isNotEmpty()) {
            messageReceiverTime.text = timeStamp.toTimeHHmmFormat()
        } else {
            messageReceiverTime.setHide()
        }
    }

    private fun MyViewHolder.initSenderMessage(
        position: Int
    ) {
        blockMessageSender.setVisibility()
        blockMessageReceiver.setHide()
        messageSender.text = messagesCache[position].text
        val timeStamp = messagesCache[position].timestamp.toString()
        if (timeStamp.isNotEmpty()) {
            messageSenderTime.text = timeStamp.toTimeHHmmFormat()
        } else {
            messageSenderTime.setHide()
        }
    }

    override fun getItemCount() = messagesCache.size

    fun setList(list: List<MessageUi>) {
        messagesCache = list
        notifyDataSetChanged()
    }
}





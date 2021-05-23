package com.example.meetupapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.R
import com.example.meetupapp.databinding.ItemContactBinding
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.ui.features.chatmeet.ChatAndMeetingFragmentDirections

class ContactAdapter(
    private val textView: TextView,
    private val items: MutableList<ContactUi>,
) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val fullName = itemBinding.textContactName
        val phoneNumber = itemBinding.textPhoneNumber
        val itemRV = itemBinding.itemRV
        val check = itemBinding.checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemContactBinding = DataBindingUtil
            .inflate(inflater, R.layout.item_contact, parent, false)
        if (items.isNotEmpty()) {
            textView.visibility = View.GONE
        }
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            fillDataViewHolder(position)

            itemRV.setOnClickListener {
                goToChatFragment(it, position)
            }

        }

    }

    private fun MyViewHolder.fillDataViewHolder(
        position: Int
    ) {
        val phone = items[position].phone
        val nickname = items[position].nickname
        fullName.text = nickname
        phoneNumber.text = phone.takeIf { phone != nickname }
    }

    override fun getItemCount(): Int = items.size

    private fun goToChatFragment(
        view: View,
        position: Int,
    ) {
        with(items[position]) {
            val contact = ContactUi(
                id = id,
                phone = phone,
                nickname = nickname,
                biography = biography,
                fullname = fullname,
                status = status,
                photoUrl = photoUrl,
            )

            view.findNavController()
                .navigate(ChatAndMeetingFragmentDirections.toChatFragment(contact))
        }
    }
}





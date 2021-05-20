package com.example.meetupapp.ui.features.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetup.pojo.ChatUi
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatsBinding
import com.example.meetupapp.ui.recyclerViewAdapter.ChatPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ChatPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatsBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chats, container, false)


        val items = emptyList<ChatUi>()
        val chat = binding.charRecyclerView
        chat.layoutManager = LinearLayoutManager(context)
        chat.adapter = ChatPagerAdapter(
            items.toMutableList(),
            activity as AppCompatActivity,
            binding.textChatsIsEmpty
        )

        return binding.root
    }
}
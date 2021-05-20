package com.example.meetupapp.ui.features.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetupapp.pojo.ChatUi
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatsBinding
import com.example.meetupapp.ui.recyclerViewAdapter.ChatPagerAdapter
import com.example.meetupapp.util.extensions.getDataModel
import com.example.meetupapp.util.firebase.AppValueEventListener
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_MESSAGES
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ChatPageFragment : Fragment() {

    private lateinit var adapter: ChatPagerAdapter
    private lateinit var binding: FragmentChatsBinding
    private val chats = MutableLiveData<ChatUi>()
    private val list = mutableListOf<ChatUi>()

    /**
     * При закрытии фрагмента необходимо очистить всех слушателей Firebase
     * Чтобы избежать в дальнейшем утечек памяти
     * */
    private lateinit var refListener: AppValueEventListener
    private var mapListener = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chats, container, false)
        initRecyclerView()
        chats.observe(viewLifecycleOwner) {
            updateRecyclerView(binding, it)
        }
        return binding.root
    }

    private fun updateRecyclerView(
        binding: FragmentChatsBinding,
        it: ChatUi
    ) {
        if (!list.contains(it)) {
            list.add(it)
        }
        val chatRecyclerView = binding.charRecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChatPagerAdapter(
            items = list,
            activity = activity as AppCompatActivity,
            textView = binding.textChatsIsEmpty
        )
        chatRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        FirebaseProvider.CURRENT_UID?.let {
            val getChatsListener = getAllChats()
            val refDatabase = FirebaseDatabase.getInstance().reference
            refDatabase.child(NODE_MESSAGES)
                .child(it)
                .addListenerForSingleValueEvent(getChatsListener)
            refListener = getChatsListener
            mapListener[refDatabase] = getChatsListener
        }
    }

    private fun getAllChats() =
        AppValueEventListener { dataSnapshot ->
            dataSnapshot.children.forEach { data ->
                val chatsValue = ChatUi(
                    fullName = data.key.toString()
                )
                chats.value = chatsValue
            }
        }

    override fun onPause() {
        super.onPause()
        mapListener.forEach {
            it.key.removeEventListener(refListener)
        }
    }
}
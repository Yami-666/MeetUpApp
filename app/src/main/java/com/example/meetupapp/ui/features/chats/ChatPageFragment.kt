package com.example.meetupapp.ui.features.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatsBinding
import com.example.meetupapp.databinding.FragmentContactsBinding
import com.example.meetupapp.pojo.ChatUi
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.ui.adapters.ChatPagerAdapter
import com.example.meetupapp.ui.adapters.ContactAdapter
import com.example.meetupapp.ui.features.contacts.ContactsFragmentDirections
import com.example.meetupapp.utils.extensions.getDataModel
import com.example.meetupapp.utils.extensions.hide
import com.example.meetupapp.utils.extensions.orIfNull
import com.example.meetupapp.utils.extensions.show
import com.example.meetupapp.utils.firebase.AppValueEventListener
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_FROM
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TEXT
import com.example.meetupapp.utils.firebase.FirebaseProvider.CHILD_TO
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MESSAGES
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ChatPageFragment : Fragment() {

    private lateinit var adapter: ContactAdapter
    private lateinit var binding: FragmentChatsBinding
    private val contacts = MutableLiveData<ContactUi>()
    private val list = mutableListOf<ContactUi>()

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
        initContactData()
        contacts.observe(viewLifecycleOwner) {
            updateRecyclerView(binding, it)
        }
        return binding.root
    }

    private fun updateRecyclerView(
        binding: FragmentChatsBinding,
        it: ContactUi
    ) {
        /**
         * Так сделано для того, чтобы при обновлении списка
         * не дублировались существующие элементы
         * */
        if (!list.contains(it)) {
            list.add(it)
        }
        val contact = binding.charRecyclerView
        contact.layoutManager = LinearLayoutManager(context)
        adapter = ContactAdapter(
            items = list,
            textView = binding.textChatsIsEmpty
        )
        contact.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initContactData() {
        FirebaseProvider.CURRENT_UID?.let {
            val contactIdListener = getContactId()
            val refDatabase = FirebaseDatabase.getInstance().reference
            refDatabase.child(FirebaseProvider.NODE_PHONE_CONTACTS)
                .child(it)
                .addValueEventListener(contactIdListener)
            mapListener[refDatabase] = contactIdListener
        }
    }

    private fun getContactId() =
        AppValueEventListener { dataSnapshot ->
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2?.let { data ->
                    val userId = data.getDataModel<ContactUi>()
                    val refDatabase =
                        FirebaseProvider.referenceDatabase.child(FirebaseProvider.NODE_USERS).child(userId.id)
                    val contactsListener = getContactValueListener()

                    mapListener[refDatabase] = contactsListener
                    refDatabase.addListenerForSingleValueEvent(contactsListener)
                }
            }
        }


    private fun getContactValueListener() = AppValueEventListener {
        val contactValue = it.getDataModel<ContactUi>()
        contacts.value = contactValue
    }

    override fun onPause() {
        super.onPause()
        mapListener.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}
package com.example.meetupapp.ui.features.contacts

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
import com.example.meetupapp.databinding.FragmentContactsBinding
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.ui.recyclerViewAdapter.ContactAdapter
import com.example.meetupapp.util.extensions.getDataModel
import com.example.meetupapp.util.extensions.hide
import com.example.meetupapp.util.firebase.FirebaseProvider.CURRENT_UID
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_PHONE_CONTACTS
import com.example.meetupapp.util.extensions.show
import com.example.meetupapp.util.firebase.AppValueEventListener
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.example.meetupapp.util.firebase.FirebaseProvider.NODE_USERS
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private lateinit var adapter: ContactAdapter
    private lateinit var binding: FragmentContactsBinding
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
            .inflate(inflater, R.layout.fragment_contacts, container, false)

        binding.contactsProgressBar.show()
        initRecyclerView()
        initTopBarBackListener(binding)
        contacts.observe(viewLifecycleOwner) {
            updateRecyclerView(binding, it)
        }
        return binding.root
    }

    private fun updateRecyclerView(
        binding: FragmentContactsBinding,
        it: ContactUi
    ) {
        list.add(it)
        val contact = binding.contactsRecyclerView
        contact.layoutManager = LinearLayoutManager(context)
        adapter = ContactAdapter(
            items = list,
            activity = activity as AppCompatActivity,
            textView = binding.text
        )
        contact.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        CURRENT_UID?.let {
            val contactIdListener = getContactId()
            val refDatabase = FirebaseDatabase.getInstance().reference
            refDatabase.child(NODE_PHONE_CONTACTS)
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
                        FirebaseProvider.referenceDatabase.child(NODE_USERS).child(userId.id)
                    val contactsListener = getContactValueListener()

                    mapListener[refDatabase] = contactsListener
                    refDatabase.addListenerForSingleValueEvent(contactsListener)
                }
            }
        }


    private fun getContactValueListener() = AppValueEventListener {
        val contactValue = it.getDataModel<ContactUi>()
        contacts.value = contactValue
        binding.contactsProgressBar.hide()
    }

    private fun initTopBarBackListener(binding: FragmentContactsBinding) {
        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController()
                .navigate(ContactsFragmentDirections.toChatAndMeeting())
        }
    }

    override fun onPause() {
        super.onPause()
        mapListener.forEach {
           it.key.removeEventListener(it.value)
        }
    }
}

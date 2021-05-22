package com.example.meetupapp.ui.features.meetings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentMeetupsBinding
import com.example.meetupapp.pojo.MeetingUi
import com.example.meetupapp.ui.adapters.MeetingAdapter
import com.example.meetupapp.utils.extensions.getDataModel
import com.example.meetupapp.utils.firebase.AppValueEventListener
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MEETINGS
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class MeetingPageFragment : Fragment() {

    private lateinit var adapter: MeetingAdapter
    private lateinit var binding: FragmentMeetupsBinding
    private val meetings = MutableLiveData<MeetingUi>()
    private val list = mutableListOf<MeetingUi>()

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
            .inflate(inflater, R.layout.fragment_meetups, container, false)
        initRecyclerView()
        meetings.observe(viewLifecycleOwner) {
            updateRecyclerView(binding, it)
        }
        return binding.root
    }

    private fun updateRecyclerView(
        binding: FragmentMeetupsBinding,
        it: MeetingUi
    ) {
        if (!list.contains(it)) {
            list.add(it)
        }
        val chatRecyclerView = binding.meetingRecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MeetingAdapter(
            items = list,
            activity = activity as AppCompatActivity,
            textView = binding.textMeetingIsEmpty
        )
        chatRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        FirebaseProvider.CURRENT_UID?.let {
            val getChatsListener = getAllMeetings()
            val refDatabase = FirebaseDatabase.getInstance().reference
            refDatabase.child(NODE_MEETINGS)
                .child(it)
                .addListenerForSingleValueEvent(getChatsListener)
            refListener = getChatsListener
            mapListener[refDatabase] = getChatsListener
        }
    }

    private fun getAllMeetings() =
        AppValueEventListener { dataSnapshot ->
            dataSnapshot.children.forEach { data ->
                val meetingValue = data.getDataModel<MeetingUi>()
                meetings.value = meetingValue
            }
        }

    override fun onPause() {
        super.onPause()
        mapListener.forEach {
            it.key.removeEventListener(refListener)
        }
    }

}

package com.example.meetupapp.ui.features.meetings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meetupapp.pojo.MeetingParams
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentMeetupsBinding
import com.example.meetupapp.ui.recyclerViewAdapter.MeetingAdapter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class MeetUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMeetupsBinding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_meetups, container, false)

        val items = emptyList<MeetingParams>()

        val chat = binding.meetingRecyclerView
        chat.layoutManager = LinearLayoutManager(context)
        chat.adapter = MeetingAdapter(
            items.toMutableList(),
            activity as AppCompatActivity,
            binding.textMeetingIsEmpty
        )

        return binding.root
    }
}
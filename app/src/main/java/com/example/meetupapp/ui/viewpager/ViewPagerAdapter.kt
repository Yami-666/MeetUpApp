package com.example.meetupapp.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meetupapp.ui.features.chats.ChatPageFragment
import com.example.meetupapp.ui.features.meetings.MeetingPageFragment
import com.example.meetupapp.utils.Constants
import java.lang.IllegalStateException

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            Constants.CHAT_POSITION -> {
                ChatPageFragment()
            }
            Constants.MEETING_POSITION -> {
                MeetingPageFragment()
            }
            else -> throw IllegalStateException("Illegal ViewPager2 Position")
        }
    }
}
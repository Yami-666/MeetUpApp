package com.example.meetupapp.ui.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meetupapp.ui.features.chats.ChatPageFragment
import com.example.meetupapp.ui.features.meetings.MeetUpFragment
import com.example.meetupapp.util.Constants
import java.lang.IllegalStateException

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            Constants.CHAT_POSITION -> {
                ChatPageFragment()
            }
            Constants.MEETING_POSITION -> {
                MeetUpFragment()
            }
            else -> throw IllegalStateException("Illegal ViewPager2 Position")
        }
    }
}
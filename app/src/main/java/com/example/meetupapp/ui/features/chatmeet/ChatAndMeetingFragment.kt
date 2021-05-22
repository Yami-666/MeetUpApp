package com.example.meetupapp.ui.features.chatmeet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.meetupapp.R
import com.example.meetupapp.databinding.FragmentChatsAndMeetingsBinding
import com.example.meetupapp.util.enum.UserStatus
import com.example.meetupapp.ui.viewPager.ViewPagerAdapter
import com.example.meetupapp.util.firebase.AppPermissions
import com.example.meetupapp.util.Constants.CHAT_POSITION
import com.example.meetupapp.util.Constants.MEETING_POSITION
import com.example.meetupapp.util.firebase.FirebaseProvider
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ChatAndMeetingFragment : Fragment() {
    private lateinit var binding: FragmentChatsAndMeetingsBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chats_and_meetings,
            container,
            false
        )

        with(binding) {
            topAppBar.setNavigationOnClickListener {
                drawerLayout.open()
            }
            initDrawerItemListener(binding)
            initUserContacts()
            createMessage()
            return root
        }
    }

    private fun createMessage() {
        binding.fabAddMessage.setOnClickListener {
            findNavController()
                .navigate(ChatAndMeetingFragmentDirections.toContactsFragment())
        }
    }

    private fun initUserContacts() {
        val mainActivity = activity as? AppCompatActivity
        mainActivity?.let {
            AppPermissions.checkPermission(AppPermissions.READ_CONTACTS, it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPagerAdapter = ViewPagerAdapter(this)
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = viewPagerAdapter
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                CHAT_POSITION -> tab.text = getString(R.string.chats)
                MEETING_POSITION -> tab.text = getString(R.string.meeting)
            }
        }
        attachTabLayoutMediator()
    }

    override fun onResume() {
        super.onResume()
        attachTabLayoutMediator()
    }

    private fun attachTabLayoutMediator() {
        if (!tabLayoutMediator.isAttached) {
            tabLayoutMediator.attach()
        }
    }
}

private fun initDrawerItemListener(binding: FragmentChatsAndMeetingsBinding) {
    val view = binding.root
    binding.navView.setNavigationItemSelectedListener { menuItem ->
        clickToMenuItem(menuItem, view)
        menuItem.isChecked = true
        binding.drawerLayout.close()
        true
    }
}

private fun clickToMenuItem(menuItem: MenuItem, view: View) {
    when (menuItem.itemId) {
        R.id.nav_contacts -> view.findNavController()
            .navigate(ChatAndMeetingFragmentDirections.toContactsFragment())
        R.id.nav_calendar -> {
            // TODO: 12.05.2021 Переход на фрагмент с календарём
        }
        R.id.nav_favorite -> {
            // TODO: 12.05.2021 Переход на фрагмент личного чата пользователя
        }
        R.id.nav_invite_people -> {
            // TODO: 12.05.2021 Переход на фрагмент для приглашения друзей
        }
        R.id.nav_settings -> {
            FirebaseProvider.authFirebase.signOut()
            UserStatus.updateUserStatus(UserStatus.OFFLINE)
            view.findNavController()
                .navigate(ChatAndMeetingFragmentDirections.toPhoneRegistration())
        }
        R.id.nav_share -> {
            // TODO: 12.05.2021 Переход на фрагмент с контактными данными автора
        }
    }
}
package com.example.meetupapp.ui.adapters

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.pojo.ChatUi
import com.example.meetupapp.R
import com.example.meetupapp.databinding.ItemChatBinding
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.ui.features.chatmeet.ChatAndMeetingFragmentDirections
import com.example.meetupapp.utils.extensions.orIfNull

class ChatPagerAdapter(
    private val items: MutableList<ChatUi>,
    private val activity: AppCompatActivity,
    private val textView: TextView
) : RecyclerView.Adapter<ChatPagerAdapter.MyViewHolder>() {

    private var isTopBarEnable: Boolean = false
    private var isSelectedAll: Boolean = false
    private var selectedItems: MutableList<ChatUi> = mutableListOf()
    private var actionMode: androidx.appcompat.view.ActionMode? = null

    inner class MyViewHolder(itemBinding: ItemChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val fullName = itemBinding.textChatName
        val lastMessage = itemBinding.textLastMessage
        val itemRV = itemBinding.itemRV
        val check = itemBinding.checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemChatBinding = DataBindingUtil
            .inflate(inflater, R.layout.item_chat, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            fillDataViewHolder(position)

            itemRV.setOnLongClickListener {
                createContextualTopBarOrClickedItem(holder)
                true
            }

            itemRV.setOnClickListener {
                doActionAfterClickedItem(it, position)
            }

            whenAllSelectClicked()
        }

    }

    private fun MyViewHolder.fillDataViewHolder(
        position: Int
    ) {
        fullName.text = items[position].fullName
        lastMessage.text = items[position].lastMessage
    }

    private fun MyViewHolder.doActionAfterClickedItem(
        view: View,
        position: Int
    ) {
        if (!isTopBarEnable) {
            openChat(view, position)
        } else {
            clickItem()
        }
        actionMode?.title = selectedItems.size.toString()
    }

    private fun MyViewHolder.createContextualTopBarOrClickedItem(
        holder: MyViewHolder
    ) {
        if (!isTopBarEnable) {
            createContextualTopBar(holder)
        } else {
            clickItem()
        }
        actionMode?.title = selectedItems.size.toString()
    }

    override fun getItemCount(): Int = items.size


    private fun createContextualTopBar(
        holder: MyViewHolder,
    ) {
        val contextualCallBack = getContextualCallback(holder)
        actionMode = activity.startSupportActionMode(contextualCallBack)
    }

    private fun MyViewHolder.whenAllSelectClicked() {
        if (isSelectedAll) {
            check.visibility = View.VISIBLE
            itemRV.setBackgroundColor(Color.LTGRAY)
        } else {
            check.visibility = View.GONE
            itemRV.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun MyViewHolder.clickItem() {
        val item = items[this.adapterPosition]
        if (check.visibility == View.GONE) {
            check.visibility = View.VISIBLE
            itemRV.setBackgroundColor(Color.LTGRAY)
            selectedItems.add(item)
        } else {
            check.visibility = View.GONE
            itemRV.setBackgroundColor(Color.TRANSPARENT)
            selectedItems.remove(item)
        }
    }

    private fun openChat(
        view: View,
        position: Int
    ) {

        with(items[position]) {
            val contact = ContactUi(
                id = this.to
            )

            view.findNavController()
                .navigate(ChatAndMeetingFragmentDirections.toChatFragment(contact))
        }

    }

    private fun getContextualCallback(holder: MyViewHolder): androidx.appcompat.view.ActionMode.Callback =
        object : androidx.appcompat.view.ActionMode.Callback {
            override fun onCreateActionMode(
                mode: androidx.appcompat.view.ActionMode?,
                menu: Menu?
            ): Boolean {
                val menuInflater = mode?.menuInflater
                menuInflater?.inflate(R.menu.contextual_menu, menu).orIfNull {
                    return false
                }
                return true
            }

            override fun onPrepareActionMode(
                mode: androidx.appcompat.view.ActionMode?,
                menu: Menu?
            ): Boolean {
                isTopBarEnable = true
                holder.clickItem()
                return false
            }

            override fun onActionItemClicked(
                mode: androidx.appcompat.view.ActionMode?,
                item: MenuItem?
            ): Boolean {
                return when (item?.itemId) {
                    R.id.delete -> {
                        onTopBarDeleteClicked()
                        true
                    }
                    R.id.selectAll -> {
                        onTopBarSelectAllClicked()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
                clearDataFromItemsList()
            }
        }

    private fun clearDataFromItemsList() {
        isTopBarEnable = false
        isSelectedAll = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun onTopBarSelectAllClicked() {
        if (selectedItems.size == items.size) {
            isSelectedAll = false
            selectedItems.clear()
        } else {
            isSelectedAll = true
            selectedItems.clear()
            selectedItems.addAll(items)
        }
        actionMode?.title = selectedItems.size.toString()
        notifyDataSetChanged()
    }

    private fun onTopBarDeleteClicked() {
        items.removeAll(selectedItems)
        actionMode?.finish()

        if (items.size == 0) {
            textView.visibility = View.VISIBLE
        }
    }
}





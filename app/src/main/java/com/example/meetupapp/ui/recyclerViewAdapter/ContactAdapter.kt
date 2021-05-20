package com.example.meetupapp.ui.recyclerViewAdapter

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.R
import com.example.meetupapp.databinding.ItemContactBinding
import com.example.meetupapp.pojo.ContactUi
import com.example.meetupapp.ui.features.chatmeet.ChatAndMeetingFragmentDirections

class ContactAdapter(
    private val activity: AppCompatActivity,
    private val textView: TextView,
    private val items: MutableList<ContactUi>,
) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    private var isTopBarEnable: Boolean = false
    private var isSelectedAll: Boolean = false
    private var selectedItems: MutableList<ContactUi> = mutableListOf()
    private var actionMode: androidx.appcompat.view.ActionMode? = null

    inner class MyViewHolder(itemBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val fullName = itemBinding.textContactName
        val phoneNumber = itemBinding.textPhoneNumber
        val itemRV = itemBinding.itemRV
        val check = itemBinding.checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemContactBinding = DataBindingUtil
            .inflate(inflater, R.layout.item_contact, parent, false)
        if (items.isNotEmpty()) {
            textView.visibility = View.GONE
        }
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
                doActionOrClickedItem(it, holder, position)
            }

            whenAllSelectClicked()
        }

    }

    private fun MyViewHolder.fillDataViewHolder(
        position: Int
    ) {
        val phone = items[position].phone
        val nickname = items[position].nickname
        fullName.text = nickname
        phoneNumber.text = phone.takeIf { phone != nickname }
    }

    private fun MyViewHolder.doActionOrClickedItem(
        view: View,
        holder: MyViewHolder,
        position: Int
    ) {
        if (!isTopBarEnable) {
            goToChatFragment(view, position)
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

    private fun goToChatFragment(
        view: View,
        position: Int,
    ) {
        with(items[position]) {
            val contact = ContactUi(
                id = id,
                phone = phone,
                nickname = nickname,
                biography = biography,
                fullname = fullname,
                status = status,
                photoUrl = photoUrl,
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
                val menuInflater = mode?.menuInflater!!
                menuInflater.inflate(R.menu.contextual_menu, menu)
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
            textView.visibility = View.GONE
        }
    }
}





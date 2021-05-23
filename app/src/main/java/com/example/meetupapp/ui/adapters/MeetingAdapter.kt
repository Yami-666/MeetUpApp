package com.example.meetupapp.ui.adapters

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meetupapp.R
import com.example.meetupapp.databinding.ItemMeetingBinding
import com.example.meetupapp.pojo.MeetingUi
import com.example.meetupapp.utils.extensions.orIfNull
import com.example.meetupapp.utils.extensions.hide
import com.example.meetupapp.utils.firebase.AppValueEventListener
import com.example.meetupapp.utils.firebase.FirebaseProvider
import com.example.meetupapp.utils.firebase.FirebaseProvider.NODE_MEETINGS

class MeetingAdapter(
    private val items: MutableList<MeetingUi>,
    private val activity: AppCompatActivity,
    private val textView: TextView
) : RecyclerView.Adapter<MeetingAdapter.MyViewHolder>() {

    private var isTopBarEnable: Boolean = false
    private var isSelectedAll: Boolean = false
    private var selectedItems: MutableList<MeetingUi> = mutableListOf()
    private var actionMode: androidx.appcompat.view.ActionMode? = null

    inner class MyViewHolder(itemBinding: ItemMeetingBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val meetingName = itemBinding.meetingNameLayout
        val dateAndTime = itemBinding.textDateTime
        val address = itemBinding.meetingAddress
        val itemRV = itemBinding.itemMeeting
        val check = itemBinding.checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMeetingBinding = DataBindingUtil
            .inflate(inflater, R.layout.item_meeting, parent, false)
        checkItemsSize()
        return MyViewHolder(binding)
    }

    private fun checkItemsSize() {
        if (items.isNotEmpty()) {
            textView.hide()
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            fillDataViewHolder(position)
            itemRV.setOnLongClickListener {
                createContextualTopBarOrClickedItem(holder)
                true
            }
            itemRV.setOnClickListener {
                doActionOrClickedItem(it, holder)
            }
            whenAllSelectClicked()
        }

    }

    private fun MyViewHolder.fillDataViewHolder(
        position: Int
    ) {
        val dateAndTimeFromItem = "${items[position].date} ${items[position].time}"
        meetingName.text = items[position].name
        dateAndTime.text = dateAndTimeFromItem
        address.text = items[position].address
    }

    private fun MyViewHolder.doActionOrClickedItem(
        view: View,
        holder: MyViewHolder
    ) {
        if (!isTopBarEnable) {
            someOtherAction(view, holder)
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

    override fun getItemCount(): Int {
        return items.size
    }


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

    private fun someOtherAction(
        view: View,
        holder: MyViewHolder
    ) {

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
        selectedItems.removeFromFirebase()
        checkItemsSize()
        actionMode?.finish()
    }
}

private fun MutableList<MeetingUi>.removeFromFirebase() {
    val removeListener = AppValueEventListener {
        it.ref.removeValue()
    }

    forEach { meetingUi ->
        FirebaseProvider.referenceDatabase
            .child(NODE_MEETINGS)
            .child(meetingUi.from)
            .child(meetingUi.id)
            .addListenerForSingleValueEvent(removeListener)

        FirebaseProvider.referenceDatabase
            .child(NODE_MEETINGS)
            .child(meetingUi.to)
            .child(meetingUi.id)
            .addListenerForSingleValueEvent(removeListener)
    }
}





package com.example.meetupapp.ui.pickers

import androidx.fragment.app.FragmentManager
import com.example.meetupapp.utils.Constants.EMPTY_STRING
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object TimePickers {
    var time = EMPTY_STRING

    fun initTimePickers(parentFragmentManager: FragmentManager) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(TIME_PICKERS_TITLE)
            .build()

        timePicker.show(parentFragmentManager, TAG)

        timePicker.addOnPositiveButtonClickListener {
            time = "${timePicker.hour}:${timePicker.minute}"
        }
        timePicker.addOnCancelListener {
            time = EMPTY_STRING
        }
    }

    private const val TAG = "TimePickers"
    private const val TIME_PICKERS_TITLE = "Введите время встречи"
}
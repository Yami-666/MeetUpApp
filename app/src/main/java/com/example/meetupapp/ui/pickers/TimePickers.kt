package com.example.meetupapp.ui.pickers

import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object TimePickers {
    var time = ""

    fun initTimePickers(parentFragmentManager: FragmentManager) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Введите время встречи")
            .build()

        timePicker.show(parentFragmentManager, "timePicker")

        timePicker.addOnPositiveButtonClickListener {
            time = "${timePicker.hour}:${timePicker.minute}"
        }
        timePicker.addOnCancelListener {
            time = ""
        }
    }
}
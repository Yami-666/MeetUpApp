package com.example.meetupapp.ui.pickers

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

object DatePickers {
    var date = ""

    fun showDatePicker(parentFragmentManager: FragmentManager) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Введите дату встречи")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(parentFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"))
            calendar.time = Date(it)
            date = getDateStringFromCalendar(calendar)
            Log.e("TAG", date)
        }
        datePicker.addOnCancelListener {
            date = ""
        }
    }

    /**
     * Date format - DD.mm.yyyy
     * */
    private fun getDateStringFromCalendar(calendar: Calendar) =
        "${calendar.get(Calendar.DAY_OF_MONTH)}." +
                "${calendar.get(Calendar.MONTH) + 1}.${calendar.get(Calendar.YEAR)}"
}
package com.example.meetupapp.ui.pickers

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.meetupapp.utils.Constants.EMPTY_STRING
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

object DatePickers {
    var date = EMPTY_STRING

    fun showDatePicker(parentFragmentManager: FragmentManager) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(DATE_PICKERS_TITLE)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(parentFragmentManager, TAG)

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_3_TIME_ZONE))
            calendar.time = Date(it)
            date = getDateStringFromCalendar(calendar)
            Log.e(TAG, date)
        }
        datePicker.addOnCancelListener {
            date = EMPTY_STRING
        }
    }

    /**
     * Date format - DD.mm.yyyy
     * */
    private fun getDateStringFromCalendar(calendar: Calendar) =
        "${calendar.get(Calendar.DAY_OF_MONTH)}." +
                "${calendar.get(Calendar.MONTH) + 1}.${calendar.get(Calendar.YEAR)}"

    private const val DATE_PICKERS_TITLE = "Введите дату встречи"
    private const val TAG = "DatePickers"
    private const val UTC_3_TIME_ZONE = "UTC+3"
}
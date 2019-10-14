package com.ganji.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment : DialogFragment() {
    interface Callbacks {
        fun onTimeSelected(time: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val time = arguments?.getSerializable(ARG_TIME) as Date
        val calendar = Calendar.getInstance()
        calendar.time = time
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMin = calendar.get(Calendar.MINUTE)
        val is24 = false

        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, min: Int->

            val resultTime : Date = GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),hour,min).time
            Log.v("timepicker", "time: $hour : $min")
            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultTime)
            }
        }


        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMin,
            is24
        )
    }

    private fun getAMPM(hour:Int):String{
        return if(hour>11)"PM" else "AM"
    }

    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}

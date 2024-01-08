package com.leping.clock

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        private const val TAG = "Utils"
    }

    object TimeUtils {
        fun validateTime(time: String): Boolean {
            return try {
                SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun isCurrentTimeInRange(startTime: String, endTime: String): Boolean {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startTimeDate = sdf.parse(startTime)
            val endTimeDate = sdf.parse(endTime)
            val now = sdf.parse(sdf.format(Date()))

            if (now.equals(startTimeDate) || now.equals(endTimeDate)) {
                return true
            }

            return if (endTimeDate.before(startTimeDate)) {
                now.after(startTimeDate) || now.before(endTimeDate)
            } else {
                now.after(startTimeDate) && now.before(endTimeDate)
            }
        }
    }
}
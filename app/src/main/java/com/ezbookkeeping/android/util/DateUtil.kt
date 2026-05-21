package com.ezbookkeeping.android.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun formatDate(date: Date): String = dateFormat.format(date)
    fun formatTime(date: Date): String = timeFormat.format(date)
    fun parseDate(str: String): Date? = dateFormat.parse(str)
    fun today(): String = formatDate(Date())
    fun monthStart(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return formatDate(cal.time)
    }
    fun monthEnd(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return formatDate(cal.time)
    }
    fun dayStart(): String = today()
    fun dayEnd(): String = today()
    fun weekStart(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        if (cal.after(Calendar.getInstance())) {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
        }
        return formatDate(cal.time)
    }
    fun weekEnd(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek + 6)
        return formatDate(cal.time)
    }
    fun yearStart(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        return formatDate(cal.time)
    }
    fun yearEnd(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, Calendar.DECEMBER)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        return formatDate(cal.time)
    }
}

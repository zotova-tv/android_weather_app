package ru.gb.weather.utils

import java.util.*

fun Date.getMillis(): Long{
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}

fun Date.addDays(days: Int): Date{
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DATE, days)
    return calendar.time
}
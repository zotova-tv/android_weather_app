package ru.gb.weather.utils

import java.util.*

fun Date.getMillis(): Long{
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}
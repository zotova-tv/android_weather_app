package ru.gb.weather.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 0,
    val feelsLike: Int = 0,
    var dateUnixTime: Long = System.currentTimeMillis() / 1000 // 1522702800
){
    private val creationDateMillis: Long = System.currentTimeMillis()
    val creationDate: Date
        get() {
            return Date(creationDateMillis)
        }
    val date: Date
        get() {
            return Date(dateUnixTime * 1000)
        }

    fun getDateFormatted(format: String = "dd MMM YYYY"): String {
        val df: DateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return df.format(date)
    }
}

fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)

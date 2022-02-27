package ru.gb.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.gb.weather.R

@Parcelize
enum class ForecastName(val timeOfDay: String, var timeOfDayCode: String): Parcelable {
    Morning("Morning", "morning"),
    Day("Day", "day"),
    Evening("Evening", "evening"),
    Night("Night", "night");
}
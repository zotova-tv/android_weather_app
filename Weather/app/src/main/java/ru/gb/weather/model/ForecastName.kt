package ru.gb.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.gb.weather.R

@Parcelize
enum class ForecastName(val timeOfDay: String): Parcelable {
    Morning("Morning"),
    Day("Day"),
    Evening("Evening"),
    Night("Night");
}
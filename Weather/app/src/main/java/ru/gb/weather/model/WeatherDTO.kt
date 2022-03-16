package ru.gb.weather.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherDTO (
    val fact: FactDTO?,
    val forecast: ForecastDTO?
) : Parcelable

@Parcelize
data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?,
    val icon: String?
) : Parcelable

@Parcelize
data class ForecastDTO(
    val date_ts: Long?,
    val week: Int?,
    var sunrise: String?,
    val sunset: String?,
    var parts: List<ForecastPartDTO> = listOf()
) : Parcelable

@Parcelize
data class ForecastPartDTO(
    var part_name: String?,
    var temp_avg: Int?,
    var icon: String? = null
) : Parcelable




package ru.gb.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.gb.weather.utils.WeatherUtils

@Parcelize
class ForecastPart(val name: ForecastName, val temperature: Int = 0, val icon: String? = null): Parcelable {
    override fun toString(): String {
        return "${name.timeOfDay} $temperature"
    }

    fun getTemperatureString() = WeatherUtils.getTemperatureString(temperature)
}
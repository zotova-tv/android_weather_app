package ru.gb.weather.model

import android.os.Parcelable
import androidx.core.content.res.TypedArrayUtils.getString
import kotlinx.android.parcel.Parcelize
import ru.gb.weather.R

@Parcelize
class ForecastPart(val name: ForecastName, val temperature: Int = 0): Parcelable {
    override fun toString(): String {
        return "${name.timeOfDay} $temperature"
    }

    fun getTemperatureString() = WeatherUtils.getTemperatureString(temperature)
}
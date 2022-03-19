package ru.gb.weather.utils

class WeatherUtils {
    companion object {
        private const val PLUS = "+"
        private const val DEGREE = "°"
        private const val EMPTY_STRING = ""

        fun getTemperatureString(temperature: Int): String {
            var tempStr = if (temperature > 0) PLUS else EMPTY_STRING
            return tempStr + temperature.toString() + DEGREE
        }
    }
}
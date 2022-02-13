package ru.gb.weather.model

class WeatherUtils {
    companion object {
        private const val PLUS = "+"
        private const val DEGREE = "°"
        private const val EMPTY_STRING = ""
        private const val MORNING = "Morning"
        private const val DAY = "Day"
        private const val EVENING = "Evening"
        private const val NIGHT = "Night"

        fun getTemperatureString(temperature: Int): String {
            var tempStr = if (temperature > 0) PLUS else EMPTY_STRING
            return tempStr + temperature.toString() + DEGREE
        }
    }
}
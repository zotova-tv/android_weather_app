package ru.gb.weather.model

data class WeatherDTO (
    val fact: FactDTO?,
    val forecast: ForecastDTO?
)

data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?
)

data class ForecastDTO(
    val date_ts: Long?,
    val week: Int?,
    var sunrise: String?,
    val sunset: String?,
    var parts: List<ForecastPartDTO>
)

data class ForecastPartDTO(
    var part_name: String?,
    var temp_avg: Int?
)

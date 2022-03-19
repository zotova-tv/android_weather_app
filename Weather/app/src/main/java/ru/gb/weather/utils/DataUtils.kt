package ru.gb.weather.utils

import ru.gb.weather.model.*

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    lateinit var weather: Weather
    if (weatherDTO.fact != null && weatherDTO.forecast != null){
        val fact: FactDTO = weatherDTO.fact
        val forecast: ForecastDTO = weatherDTO.forecast
        weather = Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!, fact.icon, forecast.date_ts!!)
        for (part in forecast.parts){
            part.temp_avg?.let { temp_avg ->
                val forecastName = when(part.part_name){
                    ForecastName.Night.timeOfDayCode -> ForecastName.Night
                    ForecastName.Evening.timeOfDayCode -> ForecastName.Evening
                    ForecastName.Morning.timeOfDayCode -> ForecastName.Morning
                    else -> ForecastName.Day
                }
                weather.forecastParts.add(ForecastPart(forecastName, temp_avg, part.icon))
            }
        }
    }else{
        weather = Weather()
    }
    return weather
}
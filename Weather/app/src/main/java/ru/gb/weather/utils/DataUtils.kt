package ru.gb.weather.utils

import ru.gb.weather.model.*
import ru.gb.weather.model.room.HistoryEntity
import ru.gb.weather.model.room.NoteEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    lateinit var weather: Weather
    if (weatherDTO.fact != null && weatherDTO.forecast != null){
        val fact: FactDTO = weatherDTO.fact
        val forecast: ForecastDTO = weatherDTO.forecast
        weather = Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!, fact.icon, forecast.date_ts!! * 1000)
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

fun convertHistoryEntityListToWeatherList(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        convertHistoryEntityToWeather(it)
    }
}

fun convertHistoryEntityToWeather(historyEntity: HistoryEntity): Weather {
    return Weather(City(historyEntity.city, 0.0, 0.0), historyEntity.temperature, 0, historyEntity.condition, dateUnixTime=historyEntity.date / 1000)
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(0, date=weather.dateUnixTime, weather.city.city, weather.temperature, weather.condition)
}

fun convertNoteEntityListToNoteList(noteEntityList: List<NoteEntity>): List<Note> {
    return noteEntityList.map {
        convertNoteEntityToNote(it)
    }
}

fun convertNoteEntityToNote(noteEntity: NoteEntity): Note {
    return Note(noteEntity.text)
}

fun convertNoteToNoteEntity(note: Note): NoteEntity {
    return NoteEntity(0, note.text)
}
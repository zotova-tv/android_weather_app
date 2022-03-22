package ru.gb.weather.repository

import ru.gb.weather.model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun getHistory(id: Long) : Weather?
    fun saveEntity(weather: Weather)
}
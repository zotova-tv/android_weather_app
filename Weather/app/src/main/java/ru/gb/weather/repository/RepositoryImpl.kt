package ru.gb.weather.repository

import ru.gb.weather.model.Weather
import ru.gb.weather.model.getRussianCities
import ru.gb.weather.model.getWorldCities

class RepositoryImpl : Repository {
    override fun getWeatherFromServer() = Weather()
    override fun getWeatherFromLocalStorageRus() = getRussianCities()
    override fun getWeatherFromLocalStorageWorld() = getWorldCities()
}
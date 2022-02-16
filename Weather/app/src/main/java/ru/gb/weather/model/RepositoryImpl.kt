package ru.gb.weather.model

import ru.gb.weather.R

class RepositoryImpl : Repository {

    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorage(): Weather {
        return when((0..1).random()){
            0 -> Weather()
            1 -> throw Exception(R.string.refresh.toString())
            else -> Weather()
        }
    }
}
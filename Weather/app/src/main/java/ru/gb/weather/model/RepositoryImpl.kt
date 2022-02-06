package ru.gb.weather.model

import org.json.JSONException

class RepositoryImpl : Repository {
    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorage(): Weather {
        return when((0..1).random()){
            0 -> Weather()
            1 -> throw Exception("Something went wrong")
            else -> Weather()
        }
    }
}
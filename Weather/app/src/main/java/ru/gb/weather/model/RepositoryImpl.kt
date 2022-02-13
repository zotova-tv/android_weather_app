package ru.gb.weather.model

import ru.gb.weather.R

class RepositoryImpl : Repository {

//    override fun getWeatherFromLocalStorage(): Weather {
//        return when((0..1).random()){
//            0 -> Weather()
//            1 -> throw Exception(R.string.refresh.toString())
//            else -> Weather()
//        }
//    }

    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> {
        return getRussianCities()
    }

    override fun getWeatherFromLocalStorageWorld(): List<Weather> {
        return getWorldCities()
    }

}
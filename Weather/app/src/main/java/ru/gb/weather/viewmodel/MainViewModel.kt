package ru.gb.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weather.repository.Repository
import ru.gb.weather.repository.RepositoryImpl

class MainViewModel(
    private val liveDataToObserve : MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
)
    : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(isRussian = false)
    fun getWeatherFromRemoteSource() = getDataFromLocalSource(isRussian = true)

    private fun getDataFromLocalSource(isRussian: Boolean) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            liveDataToObserve.postValue(AppState.Success(
                when (isRussian) {
                    true -> repositoryImpl.getWeatherFromLocalStorageRus()
                    false -> repositoryImpl.getWeatherFromLocalStorageWorld()
                }
            ))
        }.start()
    }
}
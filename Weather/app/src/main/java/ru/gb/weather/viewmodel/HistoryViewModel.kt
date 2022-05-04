package ru.gb.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weather.repository.LocalRepositoryImpl

class HistoryViewModel(
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepositoryImpl: LocalRepositoryImpl = LocalRepositoryImpl()
) : ViewModel() {

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        historyLiveData.value = AppState.Success(historyRepositoryImpl.getAllHistory())
    }

    fun getHistoryListByDatesInterval(dateFrom: Long, dateTo: Long){
        historyLiveData.value = AppState.Loading
        historyLiveData.value = AppState.Success(historyRepositoryImpl.getHistoryByDatesInterval(dateFrom, dateTo))
    }
}
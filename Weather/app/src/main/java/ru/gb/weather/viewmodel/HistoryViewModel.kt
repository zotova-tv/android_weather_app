package ru.gb.weather.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weather.repository.LocalRepositoryImpl

class HistoryViewModel(
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepositoryImpl: LocalRepositoryImpl = LocalRepositoryImpl()
) : ViewModel() {

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        val handler = Handler()
        Thread{
            val histories = historyRepositoryImpl.getAllHistory()
            handler.post {
                historyLiveData.value = AppState.Success(histories)
            }
        }.start()
    }

    fun getHistoryListByDatesInterval(dateFrom: Long, dateTo: Long){
        historyLiveData.value = AppState.Loading
        val handler = Handler()
        Thread{
            val histories = historyRepositoryImpl.getHistoryByDatesInterval(dateFrom, dateTo)
            handler.post{
                historyLiveData.value = AppState.Success(histories)
            }
        }.start()
    }
}
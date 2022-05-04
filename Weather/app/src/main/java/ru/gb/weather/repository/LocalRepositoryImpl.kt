package ru.gb.weather.repository

import android.os.Handler
import android.os.HandlerThread
import ru.gb.weather.App
import ru.gb.weather.model.Note
import ru.gb.weather.model.Weather
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.model.room.HistoryEntity
import ru.gb.weather.model.room.NoteDao
import ru.gb.weather.utils.convertHistoryEntityToWeather
import ru.gb.weather.utils.convertNoteEntityListToNoteList
import ru.gb.weather.utils.convertWeatherToEntity


private const val GET_ALL_HISTORY_THREAD_NAME = "GET_ALL_HISTORY_THREAD"
private const val GET_HISTORY_THREAD_NAME = "GET_HISTORY_THREAD"
private const val SAVE_HISTORY_THREAD_NAME = "SAVE_HISTORY_THREAD"

class LocalRepositoryImpl(
    private val localHistoryDataSource: HistoryDao = App.getHistoryDao(),
    private val localNoteDataSource: NoteDao = App.getNoteDao()
) : LocalRepository {
    override fun getAllHistory(): List<Weather> {
        val weatherList = mutableListOf<Weather>()

        val handlerThread = HandlerThread(GET_ALL_HISTORY_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val historyEntities = localHistoryDataSource.all()
            for (historyEntity: HistoryEntity in historyEntities) {
                val notes = convertNoteEntityListToNoteList(localNoteDataSource.selectAllByHistoryId(historyEntity.id))
                val weather = convertHistoryEntityToWeather(historyEntity)
                for(note: Note in notes){
                    weather.notes.add(note)
                }
                weatherList.add(weather)
            }
        }
        handlerThread.quitSafely()
        handlerThread.join()
        return weatherList
    }

    override fun getHistoryByDatesInterval(dateFrom: Long, dateTo: Long): List<Weather> {
        val weatherList = mutableListOf<Weather>()

        val handlerThread = HandlerThread(GET_ALL_HISTORY_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val historyEntities = localHistoryDataSource.selectByDatesInterval(dateFrom, dateTo)
            for (historyEntity: HistoryEntity in historyEntities) {
                val notes = convertNoteEntityListToNoteList(localNoteDataSource.selectAllByHistoryId(historyEntity.id))
                val weather = convertHistoryEntityToWeather(historyEntity)
                for(note: Note in notes){
                    weather.notes.add(note)
                }
                weatherList.add(weather)
            }
        }
        handlerThread.quitSafely()
        handlerThread.join()
        return weatherList
    }

    override fun getHistory(id: Long): Weather? {
        var weather: Weather? = null
        val handlerThread = HandlerThread(GET_HISTORY_THREAD_NAME)
        handlerThread.start()
        Handler(handlerThread.looper).post {
            val historyEntityList = localHistoryDataSource.get(id)
            if(historyEntityList.isNotEmpty()){
                val historyEntity = historyEntityList[0]
                val historyId = historyEntity.id

                weather = convertHistoryEntityToWeather(historyEntity)
                weather?.let {
                    val notesList = convertNoteEntityListToNoteList(localNoteDataSource.selectAllByHistoryId(historyId))
                    for(note: Note in notesList){
                        it.notes.add(note)
                    }
                }
            }
        }
        handlerThread.quitSafely()
        handlerThread.join()
        return weather
    }

    override fun saveEntity(weather: Weather) {
        val handlerThread = HandlerThread(SAVE_HISTORY_THREAD_NAME)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            localHistoryDataSource.insert(convertWeatherToEntity(weather))
        }
        handlerThread.quitSafely()
    }
}
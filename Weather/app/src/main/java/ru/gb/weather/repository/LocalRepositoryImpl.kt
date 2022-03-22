package ru.gb.weather.repository

import android.os.Handler
import android.os.HandlerThread
import okhttp3.internal.wait
import ru.gb.weather.App
import ru.gb.weather.model.Note
import ru.gb.weather.model.Weather
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.model.room.HistoryEntity
import ru.gb.weather.model.room.NoteDao
import ru.gb.weather.utils.convertHistoryEntityListToWeatherList
import ru.gb.weather.utils.convertHistoryEntityToWeather
import ru.gb.weather.utils.convertNoteEntityListToNoteList
import ru.gb.weather.utils.convertWeatherToEntity

class LocalRepositoryImpl(
    private val localHistoryDataSource: HistoryDao = App.getHistoryDao(),
    private val localNoteDataSource: NoteDao = App.getNoteDao()
) : LocalRepository {
    override fun getAllHistory(): List<Weather> {
        val weatherList = mutableListOf<Weather>()

        val handlerThread = HandlerThread("GET_ALL_HISTORY_THREAD")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val historyEntities = localHistoryDataSource.all()
            println("history " + historyEntities)
            for (historyEntity: HistoryEntity in historyEntities) {
                val notes = convertNoteEntityListToNoteList(localNoteDataSource.selectAllByHistoryId(historyEntity.id))
                println("Notes " + notes)
                val weather = convertHistoryEntityToWeather(historyEntity)
                for(note: Note in notes){
                    weather.notes.add(note)
                }
                weatherList.add(weather)

            }
            println("thread weather list " + weatherList)
            println(Thread.currentThread().name)
        }

        handlerThread.quitSafely()
        handlerThread.join()
        return weatherList
    }

    override fun getHistory(id: Long): Weather? {
        var weather: Weather? = null
        val handlerThread = HandlerThread("GET_HISTORY_THREAD")
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
        val handlerThread = HandlerThread("SAVE_HISTORY")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            localHistoryDataSource.insert(convertWeatherToEntity(weather))
        }
        handlerThread.quitSafely()
    }
}
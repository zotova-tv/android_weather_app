package ru.gb.weather.repository

import ru.gb.weather.App
import ru.gb.weather.model.Note
import ru.gb.weather.model.Weather
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.model.room.HistoryEntity
import ru.gb.weather.model.room.NoteDao
import ru.gb.weather.utils.convertHistoryEntityToWeather
import ru.gb.weather.utils.convertNoteEntityListToNoteList
import ru.gb.weather.utils.convertWeatherToEntity


class LocalRepositoryImpl(
    private val localHistoryDataSource: HistoryDao = App.getHistoryDao(),
    private val localNoteDataSource: NoteDao = App.getNoteDao()
) : LocalRepository {
    override fun getAllHistory(): List<Weather> {
        val weatherList = mutableListOf<Weather>()
        val historyEntities = localHistoryDataSource.all()
        for (historyEntity: HistoryEntity in historyEntities) {
            val notes = convertNoteEntityListToNoteList(
                localNoteDataSource.selectAllByHistoryId(historyEntity.id)
            )
            val weather = convertHistoryEntityToWeather(historyEntity)
            for(note: Note in notes){
                weather.notes.add(note)
            }
            weatherList.add(weather)
        }
        return weatherList
    }

    override fun getHistoryByDatesInterval(dateFrom: Long, dateTo: Long): List<Weather> {
        val weatherList = mutableListOf<Weather>()
        val historyEntities = localHistoryDataSource.selectByDatesInterval(dateFrom, dateTo)
        for (historyEntity: HistoryEntity in historyEntities) {
            val notes = convertNoteEntityListToNoteList(localNoteDataSource.selectAllByHistoryId(historyEntity.id))
            val weather = convertHistoryEntityToWeather(historyEntity)
            for(note: Note in notes){
                weather.notes.add(note)
            }
            weatherList.add(weather)
        }
        return weatherList
    }

    override fun getHistory(id: Long): Weather? {
        var weather: Weather? = null
        val historyEntityList = localHistoryDataSource.get(id)
        if(historyEntityList.isNotEmpty()){
            val historyEntity = historyEntityList[0]
            weather = convertHistoryEntityToWeather(historyEntity)
            val notesList = convertNoteEntityListToNoteList(
                localNoteDataSource.selectAllByHistoryId(historyEntity.id)
            )
            for(note: Note in notesList){
                weather.notes.add(note)
            }
        }
        return weather
    }

    override fun saveEntity(weather: Weather) {
        localHistoryDataSource.insert(convertWeatherToEntity(weather))
    }
}
package ru.gb.weather.repository

import android.os.Handler
import android.os.HandlerThread
import ru.gb.weather.App
import ru.gb.weather.model.Note
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.model.room.NoteDao
import ru.gb.weather.utils.convertNoteEntityListToNoteList
import ru.gb.weather.utils.convertNoteToNoteEntity

private const val SAVE_NOTE_THREAD_NAME = "SAVE_NOTE_THREAD"

class NoteRepositoryImpl(
    private val localNoteDataSource: NoteDao = App.getNoteDao(),
    private val localHistoryDataSource: HistoryDao = App.getHistoryDao()
) : NoteRepository {
    override fun getAllNotes(): List<Note> {
        return convertNoteEntityListToNoteList(localNoteDataSource.all())
    }

    override fun saveNote(note: Note) {
        val handlerThread = HandlerThread(SAVE_NOTE_THREAD_NAME)
        handlerThread.start()
        Handler(handlerThread.looper).post {
            note.weather?.let {
                val historyEntityItems = localHistoryDataSource.getDataByWord(it.city.city)
                if (historyEntityItems.isNotEmpty()) {
                    val noteEntityItem = convertNoteToNoteEntity(note)
                    noteEntityItem.historyId = historyEntityItems[0].id
                    localNoteDataSource.insert(noteEntityItem)
                }
            }
        }
        handlerThread.quitSafely()
    }
}
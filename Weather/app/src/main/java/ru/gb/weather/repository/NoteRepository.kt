package ru.gb.weather.repository

import ru.gb.weather.model.Note
import ru.gb.weather.model.room.NoteEntity

interface NoteRepository {
    fun getAllNotes(): List<Note>
    fun saveNote(note: Note)
}
package ru.gb.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weather.App
import ru.gb.weather.model.Note
import ru.gb.weather.repository.LocalRepositoryImpl
import ru.gb.weather.repository.NoteRepositoryImpl

private const val SAVE_ERROR_MSG = "Comment not saved"

class AddNoteViewModel(
    val noteLiveData : MutableLiveData<AppState> = MutableLiveData(),
    private val noteRepositoryImpl: NoteRepositoryImpl = NoteRepositoryImpl(App.getNoteDao())
) : ViewModel() {
    fun saveNote(note: Note){
        if(note.weather != null){
            noteRepositoryImpl.saveNote(note)
            noteLiveData.value = AppState.Success(listOf(note.weather))
        }else{
            noteLiveData.value = AppState.Error(Throwable(SAVE_ERROR_MSG))
        }
    }
}
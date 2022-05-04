package ru.gb.weather.model.room

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM NoteEntity")
    fun all() : List<NoteEntity>

    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    fun get(id: Long) : List<NoteEntity>

    @Query("SELECT * FROM NoteEntity WHERE historyId = :historyId")
    fun selectAllByHistoryId(historyId: Long): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: NoteEntity)

    @Update
    fun update(entity: NoteEntity)

    @Delete
    fun delete(entity: NoteEntity)

    @Query("DELETE FROM NoteEntity WHERE id = :id")
    fun deleteById(id: Long)
}
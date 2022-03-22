package ru.gb.weather.model.room

import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM HistoryEntity")
    fun all() : List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity WHERE id = :id")
    fun get(id: Long) : List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity WHERE city LIKE :city ORDER BY id DESC")
    fun getDataByWord(city: String): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Update
    fun update(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)

    @Query("DELETE FROM HistoryEntity WHERE id = :id")
    fun deleteById(id: Long)
}
package ru.gb.weather.model.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = HistoryEntity::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("historyId"),
    onDelete = ForeignKey.CASCADE)
])
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    var historyId: Long? = null
)

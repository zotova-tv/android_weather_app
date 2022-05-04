package ru.gb.weather.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class HistoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val city: String = "",
    val temperature: Int = 0,
    val condition: String = ""
)
package ru.gb.weather

import android.app.Application
import androidx.room.Room
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.model.room.HistoryDataBase
import ru.gb.weather.model.room.NoteDao
import java.lang.IllegalStateException


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var db : HistoryDataBase? = null
        private const val DB_NAME = "History.db"

        private fun buildDbIfNull() {
            synchronized(HistoryDataBase::class.java) {
                if (db == null) {
                    if (appInstance == null) throw IllegalStateException("APP must not be null")

                    db = Room.databaseBuilder(
                        appInstance!!.applicationContext,
                        HistoryDataBase::class.java,
                        DB_NAME
                    )
                        .build()
                }
            }
        }

        fun getHistoryDao() : HistoryDao {

            buildDbIfNull()
            return db!!.historyDao()
        }

        fun getNoteDao() : NoteDao {

            buildDbIfNull()
            return db!!.noteDao()
        }
    }

}
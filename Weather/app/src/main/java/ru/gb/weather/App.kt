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

        private fun getDb(): HistoryDataBase {
            // appInstance!!.applicationContext.deleteDatabase(DB_NAME)
            synchronized(HistoryDataBase::class.java) {
                if (db == null) {
                    if (appInstance == null) throw IllegalStateException("APP must not be null")

                    db = Room.databaseBuilder(
                        appInstance!!.applicationContext,
                        HistoryDataBase::class.java,
                        DB_NAME
                    )
                        //.allowMainThreadQueries()
                        .build()
                }
                return db!!
            }
        }

        fun getHistoryDao() : HistoryDao {

            db = getDb()

            return db!!.historyDao()
        }

        fun getNoteDao() : NoteDao {

            db = getDb()

            return db!!.noteDao()
        }
    }

}
package com.stellarworker.geonote

import android.app.Application
import androidx.room.Room
import com.stellarworker.geonote.data.room.MarkersDAO
import com.stellarworker.geonote.data.room.MarkersDataBase
import com.stellarworker.geonote.di.mainKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(mainKoinModule)
        }
    }

    companion object {
        private var appInstance: App? = null
        private var db: MarkersDataBase? = null
        private const val DB_NAME = "Markers.db"
        private const val ERROR_MESSAGE = "APP must not be null"

        fun getMarkersDAO(): MarkersDAO {
            synchronized(MarkersDataBase::class.java) {
                if (db == null) {
                    if (appInstance == null) throw IllegalStateException(ERROR_MESSAGE)
                    db = Room.databaseBuilder(
                        appInstance!!.applicationContext,
                        MarkersDataBase::class.java,
                        DB_NAME
                    ).build()
                }
            }
            return db!!.markersDAO()
        }
    }
}
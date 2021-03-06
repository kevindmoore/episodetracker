package com.mastertechsoftware.episodetracker

import android.app.Application
import com.facebook.stetho.Stetho
import com.mastertechsoftware.activity.DefaultCurrentActivityListener
import com.mastertechsoftware.episodetracker.dagger.AppComponent
import com.mastertechsoftware.episodetracker.dagger.AppModule
import com.mastertechsoftware.episodetracker.dagger.ContextModule
import com.mastertechsoftware.episodetracker.dagger.DaggerAppComponent
import com.mastertechsoftware.logging.Logger
import com.mastertechsoftware.logging.SDLogger
import com.mastertechsoftware.util.ExceptionHandler
import com.mastertechsoftware.util.NotificationHandler

/**
 * Application class
 */
class EpisodeApp  : Application() {
    private val SD_FILE_SIZE = 20000

    override fun onCreate() {
        super.onCreate()
        setLogInfo()
        graph = DaggerAppComponent.builder().appModule(AppModule(this)).contextModule(ContextModule())
                .build()

        graph.injectApplication(this)
        graph.databaseManager() // Do this so that it get's it's instance set
        val defaultCurrentActivityListener = DefaultCurrentActivityListener()
        registerActivityLifecycleCallbacks(defaultCurrentActivityListener)
        val exceptionHandler = ExceptionHandler(defaultCurrentActivityListener, Thread.getDefaultUncaughtExceptionHandler())
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
        NotificationHandler.setNotificationIcon(R.mipmap.ic_launcher)
        NotificationHandler.setAppName(getString(R.string.app_name))
        Stetho.initializeWithDefaults(this)
    }

    fun setLogInfo() {
        SDLogger.setLogFile("EpisodeTrackerLog.txt")
        SDLogger.setSDFileSize(SD_FILE_SIZE)
        SDLogger.setApplicationLogLines(5)
        SDLogger.setMaxLogLines(10)
        Logger.setApplicationTag("EpisodeTracker")
    }


    companion object {
        lateinit var graph: AppComponent
    }
}
package com.mastertechsoftware.episodetracker.dagger

import android.app.Activity
import com.mastertechsoftware.mvpframework.DataManager
import com.mastertechsoftware.mvpframework.EventManager
import com.mastertechsoftware.mvpframework.Presenter
import dagger.Module
import dagger.Provides

/**
 * Dagger 2 module
 */
@Module
class EpisodeTrackerModule(var activity : Activity, var presenter : Presenter)  {

    @ActivityScope
    @Provides
    fun providesPresenter() : Presenter {
        return presenter
    }

    @ActivityScope
    @Provides
    fun providesDataManager() : DataManager {
        return DataManager()
    }

    @ActivityScope
    @Provides
    fun providesEventManager() : EventManager {
        return EventManager()
    }

    @ActivityScope
    @Provides
    fun providesActivity() : Activity {
        return activity
    }
}
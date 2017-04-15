package com.mastertechsoftware.episodetracker.dagger

import android.app.Application
import dagger.Module
import dagger.Provides

/**
 * Module to provide the application.
 */
@Module
class AppModule(var application: Application) {

    @ApplicationScope
    @Provides
    fun providesApplication() : Application {
        return application
    }
}
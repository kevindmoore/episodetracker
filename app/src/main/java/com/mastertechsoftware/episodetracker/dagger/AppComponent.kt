package com.mastertechsoftware.episodetracker.dagger

import com.mastertechsoftware.easysqllibrary.sql.DatabaseManager
import com.mastertechsoftware.episodetracker.EpisodeApp
import com.mastertechsoftware.permissions.PermissionsManager
import com.mastertechsoftware.util.preferences.Prefs
import dagger.Component

/**
 * Component for injecting an Application Context
 */
@ApplicationScope
@Component(modules = arrayOf(AppModule::class, ContextModule::class))
interface AppComponent {
    fun injectApplication(application: EpisodeApp)
    fun prefs() : Prefs
    fun databaseManager() : DatabaseManager
    fun permissionManager() : PermissionsManager
}
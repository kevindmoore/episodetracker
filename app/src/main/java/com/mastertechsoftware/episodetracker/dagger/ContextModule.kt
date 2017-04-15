package com.mastertechsoftware.episodetracker.dagger

import android.app.Application
import com.mastertechsoftware.easysqllibrary.sql.DatabaseManager
import com.mastertechsoftware.network.NetworkManager
import com.mastertechsoftware.permissions.PermissionsManager
import com.mastertechsoftware.util.LockManager
import com.mastertechsoftware.util.preferences.Prefs
import dagger.Module
import dagger.Provides

/**
 * Module for Singleton Classes
 */
@Module
class ContextModule {

    @ApplicationScope
    @Provides
    fun providesLockManager(application : Application) : LockManager {
        LockManager.setContext(application)
        return LockManager.getInstance()
    }

    @ApplicationScope
    @Provides
    fun providesNetworkManager(application : Application) : NetworkManager {
        NetworkManager.setContext(application)
        return NetworkManager.getInstance()
    }

    @ApplicationScope
    @Provides
    fun providesPrefs(application : Application) : Prefs {
        Prefs.setContext(application)
        return Prefs.getPrefs()
    }

    @ApplicationScope
    @Provides
    fun providesDatabaseManager(application : Application) : DatabaseManager {
        return DatabaseManager.getInstance(application)
    }

    @ApplicationScope
    @Provides
    fun providesPermissionManager(application : Application) : PermissionsManager {
        return PermissionsManager.getInstance(application)
    }
}
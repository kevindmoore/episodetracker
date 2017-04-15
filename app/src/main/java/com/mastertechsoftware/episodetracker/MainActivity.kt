package com.mastertechsoftware.episodetracker

import android.Manifest
import android.os.Bundle
import com.mastertechsoftware.episodetracker.dagger.DaggerEpisodeTrackerComponent
import com.mastertechsoftware.episodetracker.dagger.EpisodeTrackerComponent
import com.mastertechsoftware.episodetracker.dagger.EpisodeTrackerModule
import com.mastertechsoftware.episodetracker.screens.EpisodeScreenManager
import com.mastertechsoftware.mvpframework.MVPActivity
import com.mastertechsoftware.mvpframework.Presenter
import com.mastertechsoftware.mvpframework.ScreenManager
import com.mastertechsoftware.permissions.PermissionsManager
import com.mastertechsoftware.util.DialogUtils

class MainActivity : MVPActivity() {
    lateinit var permissionManager : PermissionsManager
    var episodeScreenManager = EpisodeScreenManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = Presenter(this)
        graph = DaggerEpisodeTrackerComponent.builder().episodeTrackerModule(EpisodeTrackerModule(this, presenter)).build()
        graph.injectEpisodeScreenManager(episodeScreenManager)
        permissionManager = EpisodeApp.graph.permissionManager()
        super.onCreate(savedInstanceState)
        if (!permissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            DialogUtils.showOKDialog(this, getString(R.string.permissionTitle), getString(R.string.storagePermissionMsg), getString(R.string.ok)) {
                dialog, which -> PermissionsManager.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun getScreenManager(): ScreenManager {
        return episodeScreenManager
    }



    companion object {
        lateinit var graph: EpisodeTrackerComponent
        val STORAGE_PERMISSION_REQUEST_CODE = 100
    }
}

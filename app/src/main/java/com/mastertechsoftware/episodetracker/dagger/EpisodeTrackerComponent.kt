package com.mastertechsoftware.episodetracker.dagger

import com.mastertechsoftware.episodetracker.screens.EditEpisodeScreenLogic
import com.mastertechsoftware.episodetracker.screens.EpisodeScreenLogic
import com.mastertechsoftware.episodetracker.screens.EpisodeScreenManager
import com.mastertechsoftware.episodetracker.views.EditShowView
import com.mastertechsoftware.episodetracker.views.ShowView
import dagger.Component

/**
 * Dagger 2 component
 */
@ActivityScope
@Component(modules = arrayOf(EpisodeTrackerModule::class))
interface EpisodeTrackerComponent {
    fun injectEpisodeScreenManager(episodeScreenManager: EpisodeScreenManager)
    fun injectEpisodeScreenLogic(episodeScreenLogic: EpisodeScreenLogic)
    fun injectEditEpisodeScreenLogic(episodeScreenLogic: EditEpisodeScreenLogic)
    fun injectScreenView(showView: ShowView)
    fun injectEditScreenView(showView: EditShowView)
}


package com.mastertechsoftware.episodetracker.screens

import com.mastertechsoftware.episodetracker.MainActivity
import com.mastertechsoftware.episodetracker.models.Show
import com.mastertechsoftware.episodetracker.views.ShowView
import com.mastertechsoftware.events.Event
import com.mastertechsoftware.mvpframework.*
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

/**
 * Logic for memory screens
 */
class EpisodeScreenManager : ScreenManagerImpl() {
    private val episodeScreenLogic = EpisodeScreenLogic()
    private var editEpisodeScreenLogic : EditEpisodeScreenLogic? = null
    @Inject lateinit var eventManager : EventManager
    @Inject lateinit var dataManager : DataManager

    override fun currentLogic(): ScreenLogic? {
        if (stackSize() == 0) {
            return episodeScreenLogic
        }
        return super.currentLogic()
    }

    override fun start() {
        MainActivity.graph.injectEpisodeScreenLogic(episodeScreenLogic)
        push(episodeScreenLogic)
        eventManager.register(ScreenChannel, this)
    }

    override fun stop() {
        eventManager.unregister(ScreenChannel, this)
    }

    @Subscribe
    fun onEvent(event: Event) {
        when (event.eventType) {
            ShowView.EPISODE_CLICK ->  {
                val show = event.data as Show
                if (editEpisodeScreenLogic == null) {
                    editEpisodeScreenLogic = EditEpisodeScreenLogic()
                    MainActivity.graph.injectEditEpisodeScreenLogic(editEpisodeScreenLogic!!)
                }
                editEpisodeScreenLogic?.show = show
                push(editEpisodeScreenLogic!!)
            }
            SAVE_EPISODE ->  {
                val show = event.data as Show
                dataManager.updateItem(ShowView.DBNAME, Show::class.java, show)
                episodeScreenLogic.refresh()
                pop()
            }
            CANCEL_EPISODE ->  {
                pop()
            }
            DELETE_EPISODE -> kotlin.run {
                val show = event.data as Show
                dataManager.deleteItem(ShowView.DBNAME, Show::class.java, show.id)
                episodeScreenLogic.refresh()
                pop()
            }
        }
    }

    companion object {
        val ScreenChannel = Channel("SCREENS")
        val DELETE_EPISODE = "DELETE_EPISODE"
        val CANCEL_EPISODE = "CANCEL_EPISODE"
        val SAVE_EPISODE = "SAVE_EPISODE"

    }
}
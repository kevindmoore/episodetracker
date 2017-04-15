package com.mastertechsoftware.episodetracker.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mastertechsoftware.episodetracker.R
import com.mastertechsoftware.episodetracker.models.Show
import com.mastertechsoftware.episodetracker.screens.EpisodeScreenManager
import com.mastertechsoftware.episodetracker.views.ShowView
import com.mastertechsoftware.events.Event
import com.mastertechsoftware.logging.Logger
import com.mastertechsoftware.mvpframework.EventManager
import com.mastertechsoftware.recyclerview.DefaultViewHolder

/**
 * Adapter for showing episodes
 */
class ShowAdapter(var shows : MutableList<Show>, var eventManager : EventManager) : RecyclerView.Adapter<DefaultViewHolder>() {
    var context : Context? = null

    override fun getItemCount(): Int {
        return shows.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolder?, position: Int) {
        val show = shows[position]
        holder?.itemView?.tag = holder
        holder?.setText(R.id.episode, show.episodeName)
        holder?.setText(R.id.download_count, show.downloadCount.toString())
        holder?.setText(R.id.watched_count, show.watchedCount.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DefaultViewHolder {
        if (context == null) {
            context = parent?.context
        }
        val layoutInflator = LayoutInflater.from(context)
        val returnView = layoutInflator.inflate(R.layout.show_layout, parent, false)
        returnView.setOnClickListener {
            v ->
            run {
                val holder = v.tag as DefaultViewHolder
                val show = shows[holder.adapterPosition]
                Logger.debug("OnClick ${holder.adapterPosition} ${show.episodeName}")
                eventManager.post(EpisodeScreenManager.ScreenChannel, Event(ShowView.EPISODE_CLICK, null, show))
            }
        }
        return DefaultViewHolder(returnView)
    }

    fun addEpisode(show: Show) {
        shows.add(show)
        notifyItemInserted(shows.size-1)
    }

    fun updateShows(shows: MutableList<Show>) {
        this.shows = shows
        notifyDataSetChanged()
    }
}
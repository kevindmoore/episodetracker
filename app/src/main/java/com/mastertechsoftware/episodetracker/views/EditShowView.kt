package com.mastertechsoftware.episodetracker.views

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import com.mastertechsoftware.activity.ActivityUtils
import com.mastertechsoftware.episodetracker.MainActivity
import com.mastertechsoftware.episodetracker.R
import com.mastertechsoftware.episodetracker.models.Show
import com.mastertechsoftware.episodetracker.screens.EpisodeScreenManager
import com.mastertechsoftware.events.Event
import com.mastertechsoftware.mvpframework.DataManager
import com.mastertechsoftware.mvpframework.EventManager
import com.mastertechsoftware.mvpframework.MVPView
import com.mastertechsoftware.mvpframework.Presenter
import com.mastertechsoftware.recyclerview.DefaultViewHolder
import com.mastertechsoftware.util.Utils
import com.shawnlin.numberpicker.NumberPicker
import javax.inject.Inject

/**
 * Edit Show View
 */
class EditShowView : MVPView() {
    @Inject lateinit var presenter : Presenter
    @Inject lateinit var eventManager : EventManager
    @Inject lateinit var dataManager : DataManager
    var show : Show? = null
    var holder : DefaultViewHolder? = null

    init {
        MainActivity.graph.injectEditScreenView(this)
        presenter.toolbarManager.setTitle(R.string.edit_show)
    }

    override fun onResume() {
        presenter.showFAB(false)
        presenter.clearMenu()
    }

    override fun bindView(data: Any?) {
        show = data as Show
        holder = DefaultViewHolder(view)
        show?.let {
            holder?.setText(R.id.episode, show?.episodeName)
            val download_count = holder?.getView(R.id.download_count) as NumberPicker
            // This is a hack. Can't get to child any other way
            val inputText = Utils.findTextView(download_count)
            inputText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.0f)
            inputText?.setTextColor(Color.BLACK)
            download_count.minValue = 0
            download_count.maxValue = 50
            download_count.value = show!!.downloadCount
            val watched_count = holder?.getView(R.id.watched_count) as NumberPicker
            watched_count.minValue = 0
            watched_count.maxValue = 50
            watched_count.value = show!!.watchedCount
        }
        holder?.setClickListener(R.id.cancel, View.OnClickListener {
            cancel()
        })
        holder?.setClickListener(R.id.save, View.OnClickListener {
            ActivityUtils.hideKeyboard(presenter.activity)
            val download_count = holder?.getView(R.id.download_count) as NumberPicker
            val watched_count = holder?.getView(R.id.watched_count) as NumberPicker
            show?.downloadCount = download_count.value
            show?.watchedCount = watched_count.value
            show?.episodeName = holder?.getText(R.id.episode)
            eventManager.post(EpisodeScreenManager.ScreenChannel, Event(EpisodeScreenManager.SAVE_EPISODE, null, show))
        })
        holder?.setClickListener(R.id.delete, View.OnClickListener {
            ActivityUtils.hideKeyboard(presenter.activity)
            eventManager.post(EpisodeScreenManager.ScreenChannel, Event(EpisodeScreenManager.DELETE_EPISODE, null, show))
        })
    }
    override fun onBackPressed(): Boolean {
        cancel()
        return true
    }

    fun cancel() {
        ActivityUtils.hideKeyboard(presenter.activity)
        eventManager.post(EpisodeScreenManager.ScreenChannel, Event(EpisodeScreenManager.CANCEL_EPISODE))
    }
}
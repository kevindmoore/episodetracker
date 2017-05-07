package com.mastertechsoftware.episodetracker.views

import android.graphics.Color
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ToggleButton
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
    var downloadCount : NumberPicker? = null
    var watchedCount : NumberPicker? = null
    var season : EditText? = null
    var finishedButton : ToggleButton? = null

    init {
        MainActivity.graph.injectEditScreenView(this)
        presenter.toolbarManager.setTitle(R.string.edit_show)
    }

    override fun onResume() {
        presenter.showFAB(false)
        presenter.setMenu(R.menu.edit_menu)
    }

    override fun bindView(data: Any?) {
        show = data as Show
        holder = DefaultViewHolder(view)
        show?.let {
            holder?.setText(R.id.episode, it.episodeName)
            downloadCount = holder?.getView(R.id.download_count) as NumberPicker
            // This is a hack. Can't get to child any other way
            val inputText = Utils.findTextView(downloadCount)
            inputText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.0f)
            inputText?.setTextColor(Color.BLACK)
            downloadCount?.minValue = 0
            downloadCount?.maxValue = 50
            downloadCount?.value = it.downloadCount
            watchedCount = holder?.getView(R.id.watched_count) as NumberPicker
            watchedCount?.minValue = 0
            watchedCount?.maxValue = 50
            watchedCount?.value = it.watchedCount
            season = holder?.getView(R.id.season) as EditText
            season?.setText(it.season.toString())
            finishedButton = holder?.getView(R.id.finished) as ToggleButton
            finishedButton?.setChecked(it.finished)
        }
        holder?.setClickListener(R.id.cancel, View.OnClickListener {
            cancel()
        })
        holder?.setClickListener(R.id.save, View.OnClickListener {
            save()
        })
        holder?.setClickListener(R.id.delete, View.OnClickListener {
            delete()
        })
    }

    private fun delete() {
        ActivityUtils.hideKeyboard(presenter.activity)
        eventManager.post(EpisodeScreenManager.ScreenChannel, Event(EpisodeScreenManager.DELETE_EPISODE, null, show))
    }

    private fun save() {
        ActivityUtils.hideKeyboard(presenter.activity)
        show?.downloadCount = downloadCount?.value ?: 0
        show?.watchedCount = watchedCount?.value ?: 0
        show?.episodeName = holder?.getText(R.id.episode)
        show?.season = season?.text.toString().toInt()
        eventManager.post(EpisodeScreenManager.ScreenChannel, Event(EpisodeScreenManager.SAVE_EPISODE, null, show))
    }

    override fun onMenuClicked(menuItem: MenuItem) : Boolean {
        when (menuItem.itemId) {
            R.id.save_menu -> save()
            R.id.cancel_menu-> cancel()
            R.id.delete_menu -> delete()
            else -> return false
        }
        return true
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
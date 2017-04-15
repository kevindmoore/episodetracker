package com.mastertechsoftware.episodetracker.screens

import android.view.animation.AlphaAnimation
import com.mastertechsoftware.episodetracker.R
import com.mastertechsoftware.episodetracker.views.EditShowView
import com.mastertechsoftware.episodetracker.views.ShowView
import com.mastertechsoftware.mvpframework.*
import javax.inject.Inject

/**
 * Logic screen for The first memory screen
 */
class EpisodeScreenLogic : AbstractScreenLogic() {
    var screenManagerImpl: ScreenManager? = null
    var showView: ShowView? = null
    @Inject lateinit var presenter: Presenter

    override fun start() {
        super.start()
        val viewModel = ViewModel("ShowView", R.layout.recycler_layout)
        viewModel.viewType = ShowView::class.java as Class<MVPView>
        showView = presenter.showView(viewModel, null) as ShowView
        presenter.toolbarManager.setTitle(R.string.episodes)
    }

    override fun stop() {
    }

    override fun setScreenManager(screenManager: ScreenManager) {
        screenManagerImpl = screenManager
    }

    override fun resume() {
        if (!presenter.isCurrentView(showView)) {
            presenter.replaceView(showView!!, true)
        }
    }

    override fun pause() {
        showView?.onPause()
    }

    fun refresh() {
        showView?.refresh()
    }

}
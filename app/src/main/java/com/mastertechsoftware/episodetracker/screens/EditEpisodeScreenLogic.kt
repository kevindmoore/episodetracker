package com.mastertechsoftware.episodetracker.screens

import com.mastertechsoftware.episodetracker.R
import com.mastertechsoftware.episodetracker.models.Show
import com.mastertechsoftware.episodetracker.views.EditShowView
import com.mastertechsoftware.mvpframework.*
import javax.inject.Inject

/**
 * Logic screen for The first memory screen
 */
class EditEpisodeScreenLogic : AbstractScreenLogic() {
    var screenManagerImpl: ScreenManager? = null
    var show : Show? = null
        set(value) {
            field = value
            showView?.bindView(value)
        }
    var showView: EditShowView? = null
    @Inject lateinit var presenter: Presenter

    override fun start() {
        super.start()
        val viewModel = ViewModel("EditShowView", R.layout.edit_episode)
        viewModel.viewType = EditShowView::class.java as Class<MVPView>
        showView = presenter.showView(viewModel, show) as EditShowView
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
        showView?.show = show
        showView?.onResume()
    }

    override fun pause() {
        showView?.onPause()
    }


}
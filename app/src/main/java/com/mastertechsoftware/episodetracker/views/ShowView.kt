package com.mastertechsoftware.episodetracker.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.provider.DocumentsContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.mastertechsoftware.activity.ActivityUtils
import com.mastertechsoftware.dialog.ProgressDialogHelper
import com.mastertechsoftware.episodetracker.EpisodeApp
import com.mastertechsoftware.episodetracker.MainActivity
import com.mastertechsoftware.episodetracker.R
import com.mastertechsoftware.episodetracker.adapters.ShowAdapter
import com.mastertechsoftware.episodetracker.models.Show
import com.mastertechsoftware.logging.Logger
import com.mastertechsoftware.mvpframework.DataManager
import com.mastertechsoftware.mvpframework.EventManager
import com.mastertechsoftware.mvpframework.MVPView
import com.mastertechsoftware.mvpframework.Presenter
import com.mastertechsoftware.util.AbstractAsync
import com.mastertechsoftware.util.Utils
import com.mastertechsoftware.util.preferences.Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Show list of episodes
 */
class ShowView : MVPView() {
    @Inject lateinit var presenter: Presenter
    @Inject lateinit var eventManager: EventManager
    @Inject lateinit var dataManager: DataManager
    lateinit var prefs : Prefs
    var showList: RecyclerView? = null
    var showAdapter: ShowAdapter

    init {
        MainActivity.graph.injectScreenView(this)
        prefs = EpisodeApp.graph.prefs()
        dataManager.addDatabase(DBNAME, TABLENAME, Show::class.java)
        var shows = dataManager.getAllItems(DBNAME, Show::class.java) as MutableList<Show>
        if (prefs.getBoolean(SORTED)) {
            shows = sortEpisodes(shows)
        }
        showAdapter = ShowAdapter(shows, eventManager)
        presenter.toolbarManager.setTitle(R.string.episodes)
        presenter.toolbarManager.lockDrawer()
        presenter.setFABDrawable(R.drawable.fab_plus)
        presenter.setFABOnClickListener { addEpisode() }
        presenter.toolbarManager.setNavMenu(R.menu.nav_menu)
    }

    override fun onResume() {
        presenter.showFAB(true)
        presenter.setMenu(R.menu.sorted_menu)
        var sorted = prefs.getBoolean(SORTED)
        presenter.toolbarManager.getToolbarMenu().findItem(R.id.sort).setChecked(sorted)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.importEpisodesMenu -> startImportEpisodes()
            R.id.exportEpisodesMenu -> exportEpisodes()
            R.id.deleteEpisodesMenu -> deleteEpisodes()
        }
        return true
    }

    override fun onMenuClicked(menuItem: MenuItem) : Boolean {
        when (menuItem.itemId) {
            R.id.sort -> {
                menuItem.isChecked = !menuItem.isChecked
                prefs.putBoolean(SORTED, menuItem.isChecked)
                when (menuItem.isChecked) {
                    true -> {
                        var shows = sortEpisodes(showAdapter.shows)
                        showAdapter.updateShows(shows)
                    }
                    false -> {
                        var shows = dataManager.getAllItems(DBNAME, Show::class.java) as MutableList<Show>
                        showAdapter.updateShows(shows)
                    }
                }
                return true
            }
        }

        return false
    }

    private fun sortEpisodes(shows : MutableList<Show>) : MutableList<Show> {
        return shows.sortedWith(compareBy{ it.episodeName }) as MutableList<Show>
    }

    private fun deleteEpisodes() {
        dataManager.deleteDatabase(DBNAME)
        showAdapter.updateShows(ArrayList<Show>())
    }

    fun exportEpisodes() {
        val exportDirectory = EPISODE_PATH + dateFormat.format(Date())
        if (!Utils.ensurePathExists(exportDirectory)) {
            val toast = Toast.makeText(presenter.activity, R.string.export_error, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        val directory = File(exportDirectory)
        if (!directory.exists() && !directory.mkdir()) {
            val toast = Toast.makeText(presenter.activity, R.string.export_error, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        ProgressDialogHelper.showProgressFragment(presenter.activity, presenter.activity.getString(R.string.loading))
        try {
            val gson = GsonBuilder().create()
            val episodeFile = FileWriter(File(exportDirectory, "episodes.json"))
            val shows = JSONArray()
            for (show in showAdapter.shows) {
                shows.put(JSONObject(gson.toJson(show)))
            }
            episodeFile.write(shows.toString())
            episodeFile.close()
        } catch (e: Exception) {
            Logger.error("Problems exporting", e)
        }
        ProgressDialogHelper.hideProgressFragment(presenter.activity)

    }

    fun startImportEpisodes() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        presenter.activity.startActivityForResult(intent, FILE_BROWSER_RESULT)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK && requestCode == FILE_BROWSER_RESULT) {
            presenter.activity.runOnUiThread(object : Runnable {
                override fun run() {
                    val uri = data?.getData()
                    @SuppressLint("NewApi")
                    val parentDocumentId = DocumentsContract.getTreeDocumentId(uri)
                    val dir = File(parentDocumentId)
                    if (!dir.exists()) {
                        Utils.showLongSnackbar(view, presenter.activity.getString(R.string.directoryDoesNotExists), null, null)
                        return
                    }
                    importMemories(dir)
                }
            })
            return true
        }
        return false
    }

    private fun importMemories(dir: File) {
        if (!dir.exists()) {
            val toast = Toast.makeText(presenter.activity, R.string.import_error, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return
        }
        ProgressDialogHelper.showProgressFragment(presenter.activity, presenter.activity.getString(R.string.loading))
        val async = object : AbstractAsync() {
            override fun doBackground() {
                try {
                    val episodeFile = BufferedReader(FileReader(File(dir, "episodes.json")))
                    var line = episodeFile.readLine()
                    val episodeJson = StringBuilder()
                    while (line != null) {
                        episodeJson.append(line)
                        line = episodeFile.readLine()
                    }
                    episodeFile.close()
                    val showsArray = JSONArray(episodeJson.toString())
                    val gson = GsonBuilder().create()
                    val shows: MutableList<Show> = ArrayList()
                    for (i in 0..showsArray.length()-1) {
                        val show = gson.fromJson(showsArray[i].toString(), Show::class.java)
                        dataManager.addItem(DBNAME, Show::class.java, show)
                        shows.add(show)
                    }
                    getHolder().addResult(shows)
                } catch (e: Exception) {
                    Logger.error("Problems importing", e)
                    showList?.post {
                        val toast = Toast.makeText(presenter.activity, R.string.import_error, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
            }

            override fun finished() {
                showAdapter.updateShows(getHolder().results as MutableList<Show>)
                ProgressDialogHelper.hideProgressFragment(presenter.activity)
            }
        }

        async.execute()

    }

    fun addEpisode() {
        val inflater = LayoutInflater.from(presenter.activity)
        val addView = inflater.inflate(R.layout.add_episode, null)
        // Bring up dialog asking if they want to add the podcast
        val builder = AlertDialog.Builder(presenter.activity).setTitle(R.string.add_episode).setView(addView).setPositiveButton(R.string.save,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val name = addView.findViewById(R.id.episode) as EditText
                        val nameText = name.text.toString()
                        if (!TextUtils.isEmpty(nameText)) {
                            val show = Show()
                            show.episodeName = nameText.trim()
                            val id = dataManager.addItem(DBNAME, Show::class.java, show)
                            if (id != -1) {
                                show.id = id
                                showAdapter.addEpisode(show)
                            }
                        }
                    }
                }).setNegativeButton(R.string.cancel, null)
        val alertDialog = builder.create()
        alertDialog.setOnShowListener({ ActivityUtils.showKeyboard(presenter.activity, addView.findViewById(R.id.episode)) })
        alertDialog.show()

    }

    override fun setupView(view : View) {
        super.setupView(view)
        showList = view.findViewById(R.id.recycler_view) as RecyclerView?
        val layoutManager = LinearLayoutManager(presenter.activity)
        showList?.setLayoutManager(layoutManager)
        showList?.adapter = showAdapter
    }

    companion object {
        val SORTED = "SORTED"
        val DBNAME = "Episodes"
        val TABLENAME = "Episodes"
        val EPISODE_CLICK = "EPISODE_CLICK"
        val FILE_BROWSER_RESULT = 100
        val EPISODE_PATH = Environment.getExternalStorageDirectory().absolutePath + "/episodes/"
        private val dateFormat = SimpleDateFormat("MM-dd-yy", Locale.US)
    }

    fun refresh() {
        var shows = dataManager.getAllItems(DBNAME, Show::class.java) as MutableList<Show>
        if (prefs.getBoolean(SORTED)) {
            shows = sortEpisodes(shows)
        }
        showAdapter.updateShows(shows)
    }
}
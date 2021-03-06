package com.mastertechsoftware.episodetracker.models

import com.mastertechsoftware.easysqllibrary.sql.DefaultReflectTable

/**
 * Hold Show info
 */
class Show() : DefaultReflectTable() {
    var episodeName : String? =  null
    var season : Int =  1
    var downloadCount : Int =  0
    var watchedCount: Int =  0
    var finished: Boolean = false

    constructor(episodeName : String, downloadCount : Int,
                watchedCount: Int) : this() {
        this.episodeName = episodeName
        this.downloadCount = downloadCount
        this.watchedCount = watchedCount
    }
}
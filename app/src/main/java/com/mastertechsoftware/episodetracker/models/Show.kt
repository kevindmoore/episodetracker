package com.mastertechsoftware.episodetracker.models

import com.mastertechsoftware.easysqllibrary.sql.DefaultReflectTable

/**
 *
 */
class Show() : DefaultReflectTable() {
    var episodeName : String? =  null
    var downloadCount : Int =  0
    var watchedCount: Int =  0

    constructor(episodeName : String, downloadCount : Int,
                watchedCount: Int) : this() {
        this.episodeName = episodeName
        this.downloadCount = downloadCount
        this.watchedCount = watchedCount
    }
}
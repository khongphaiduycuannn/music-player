package com.notmyexample.musicplayer.data.data_source

import android.net.Uri
import com.notmyexample.musicplayer.data.model.Song

interface SongDataSource {

    fun getSongs(): List<Song>
}
package com.notmyexample.musicplayer.data.repository

import com.notmyexample.musicplayer.data.data_source.SongDataSource
import com.notmyexample.musicplayer.data.model.Song

class SongRepository(private val songDataSource: SongDataSource) {

    fun getSongs(): List<Song> = songDataSource.getSongs()
}
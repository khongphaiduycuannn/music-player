package com.notmyexample.musicplayer.data.repository

import com.notmyexample.musicplayer.data.data_source.SongDataSource
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song

class SongRepository(private val songDataSource: SongDataSource) {

    suspend fun getSongs(): List<Song> = songDataSource.getSongs()

    suspend fun getAlbums(): List<Album> = songDataSource.getAlbums()

    fun favouriteSong(song: Song): Boolean = songDataSource.favouriteSong(song)
}
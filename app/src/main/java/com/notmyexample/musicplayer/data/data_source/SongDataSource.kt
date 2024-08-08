package com.notmyexample.musicplayer.data.data_source

import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song

interface SongDataSource {

    suspend fun getSongs(): List<Song>

    suspend fun getAlbums(): List<Album>

    fun favouriteSong(song: Song): Boolean

    fun saveSearchResult(song: Song)

    fun getLastSearchResult(): List<Long>

    fun deleteSearchResult(song: Song)
}
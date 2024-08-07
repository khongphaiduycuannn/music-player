package com.notmyexample.musicplayer.presentation.ui.detail_album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song

class DetailAlbumViewModel : ViewModel() {

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    fun setAlbum(album: Album) {
        _album.value = album
    }

    fun setSongs(songs: List<Song>) {
        _songs.value = songs
    }
}
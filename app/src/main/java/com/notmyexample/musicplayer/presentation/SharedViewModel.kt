package com.notmyexample.musicplayer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.data.model.Song

class SharedViewModel : ViewModel() {

    private val _currentPlay = MutableLiveData<Song>()
    val currentPlay: LiveData<Song> = _currentPlay

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun setCurrentPlay(song: Song) {
        _currentPlay.value = song
    }

    fun setPlaylist(playlist: Playlist) {
        _playlist.value = playlist
    }
}
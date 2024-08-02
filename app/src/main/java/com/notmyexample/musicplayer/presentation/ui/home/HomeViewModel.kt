package com.notmyexample.musicplayer.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.use_case.song.SongUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songUseCases: SongUseCases
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    private var getSongsJob: Job? = null
    private var getAlbumsJob: Job? = null

    init {
        getSongs()
        getAlbums()
    }

    fun getSongs() {
        getSongsJob?.cancel()

        getSongsJob = viewModelScope.launch(Dispatchers.IO) {
            _songs.postValue(songUseCases.getSongs())
        }
    }

    fun getAlbums() {
        getAlbumsJob?.cancel()

        getAlbumsJob = viewModelScope.launch(Dispatchers.IO) {
            _albums.postValue(songUseCases.getAlbums())
        }
    }
}
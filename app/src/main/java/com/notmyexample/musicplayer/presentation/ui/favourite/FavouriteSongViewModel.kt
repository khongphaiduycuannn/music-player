package com.notmyexample.musicplayer.presentation.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.use_case.song.SongUseCases
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.AUTHOR
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.DEFAULT
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteSongViewModel @Inject constructor(
    private val songUseCases: SongUseCases
) : ViewModel() {

    val album = Album(-1, "Favourite songs")

    private val _favouriteSongs = MutableLiveData<List<Song>>()
    val favouriteSongs: LiveData<List<Song>> = _favouriteSongs

    private val _sortedFavouriteSong = MutableLiveData<List<Song>>()
    val sortedFavouriteSong: LiveData<List<Song>> = _sortedFavouriteSong

    private val _searchKey = MutableLiveData<String>(null)
    val searchKey: LiveData<String> = _searchKey

    private val _sortType = MutableLiveData(DEFAULT)
    val sortType: LiveData<String> = _sortType

    fun getFavouriteSongs() {
        viewModelScope.launch { _favouriteSongs.value = songUseCases.getFavouriteSongs() }
    }

    fun setSearchKey(key: String) {
        _searchKey.value = key
    }

    fun setSortType(sortType: String) {
        _sortType.value = sortType
    }

    fun sortSongs(sortType: String) {
        when (sortType) {
            DEFAULT -> _sortedFavouriteSong.value = _favouriteSongs.value?.toList()
            NAME -> _sortedFavouriteSong.value = _favouriteSongs.value?.sortedBy { it.name }
            AUTHOR -> _sortedFavouriteSong.value = _favouriteSongs.value?.sortedBy { it.author }
        }
    }
}
package com.notmyexample.musicplayer.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.use_case.song.SongUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "SearchViewModel"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val songUseCases: SongUseCases
) : ViewModel() {

    private val _lastSearchResult = MutableLiveData<List<Long>>()
    val lastSearchResult: LiveData<List<Long>> = _lastSearchResult

    private val _searchKey = MutableLiveData<String>(null)
    val searchKey: LiveData<String> = _searchKey

    fun getLastSearchResult() {
        _lastSearchResult.value = songUseCases.getLastSearchResult()
    }

    fun saveSearchResult(song: Song) {
        songUseCases.saveSearchResult(song)
        getLastSearchResult()
    }

    fun deleteSearchResult(song: Song) {
        songUseCases.deleteSearchResult(song)
        getLastSearchResult()
    }

    fun setSearchKey(key: String) {
        _searchKey.value = key
    }
}
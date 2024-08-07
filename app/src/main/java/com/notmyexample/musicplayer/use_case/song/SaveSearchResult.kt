package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.data.repository.SongRepository

data class SaveSearchResult(
    private val songRepository: SongRepository
) {

    operator fun invoke(song: Song) = songRepository.saveSearchResult(song)
}
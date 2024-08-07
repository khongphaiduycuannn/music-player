package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.repository.SongRepository

data class GetLastSearchResult(
    private val songRepository: SongRepository
) {

    operator fun invoke() = songRepository.getLastSearchResult()
}
package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.repository.SongRepository

class GetSongs(
    private val songRepository: SongRepository
) {
    operator fun invoke() = songRepository.getSongs()
}
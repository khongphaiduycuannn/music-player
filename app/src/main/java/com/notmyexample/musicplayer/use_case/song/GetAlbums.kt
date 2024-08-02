package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.repository.SongRepository

class GetAlbums(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke() = songRepository.getAlbums()
}
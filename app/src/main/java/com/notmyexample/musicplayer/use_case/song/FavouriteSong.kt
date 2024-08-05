package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.data.repository.SongRepository

class FavouriteSong(
    private val songRepository: SongRepository
) {
    operator fun invoke(song: Song): Boolean = songRepository.favouriteSong(song)
}
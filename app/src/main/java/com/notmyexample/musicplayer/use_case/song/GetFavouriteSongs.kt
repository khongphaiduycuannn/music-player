package com.notmyexample.musicplayer.use_case.song

import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.data.repository.SongRepository

data class GetFavouriteSongs(
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(): List<Song> = songRepository.getFavouriteSongs()
}
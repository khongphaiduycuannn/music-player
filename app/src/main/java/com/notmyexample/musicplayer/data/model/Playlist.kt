package com.notmyexample.musicplayer.data.model

data class Playlist(
    var name: String?,
    var songs: List<Song>,
    var originalSongs: List<Song> = songs.toList(),
    var isShuffled: Boolean = false
)
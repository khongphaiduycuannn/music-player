package com.notmyexample.musicplayer.data.model

data class Playlist(
    var name: String?,
    val songs: List<Song>
)
package com.notmyexample.musicplayer.use_case.song

data class SongUseCases(
    val getSongs: GetSongs,
    val getFavouriteSongs: GetFavouriteSongs,
    val getAlbums: GetAlbums,
    val favouriteSong: FavouriteSong,
    val saveSearchResult: SaveSearchResult,
    val getLastSearchResult: GetLastSearchResult,
    val deleteSearchResult: DeleteSearchResult
)
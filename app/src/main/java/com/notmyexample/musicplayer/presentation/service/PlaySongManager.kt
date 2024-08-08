package com.notmyexample.musicplayer.presentation.service

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.use_case.song.SongUseCases
import com.notmyexample.musicplayer.utils.loop
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

private const val TAG = "PlaySongManager"

@Singleton
class PlaySongManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val songUseCases: SongUseCases
) {

    private var mediaPlayer: MediaPlayer? = null

    val songsLiveData = MutableLiveData(listOf<Song>())
    val albumsLiveData = MutableLiveData(listOf<Album>())
    val playlistLiveData = MutableLiveData<Playlist>()

    val currentAlbum = MutableLiveData<Album?>(null)
    val currentPlayLiveData = MutableLiveData<Song?>(null)
    val currentPositionLiveData = MutableLiveData(0)

    val isPlayingLiveData = MutableLiveData(false)
    val isLoopingLiveData = MutableLiveData(false)

    private var getSongsJob: Job? = null
    private var getAlbumsJob: Job? = null

    private var handler: Handler? = null

    init {
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { playNextSong() }
        }
    }

    fun getSongs() {
        getSongsJob?.cancel()

        getSongsJob = CoroutineScope(Dispatchers.IO).launch {
            songsLiveData.postValue(songUseCases.getSongs())
        }
    }

    fun getAlbums() {
        getAlbumsJob?.cancel()

        getAlbumsJob = CoroutineScope(Dispatchers.IO).launch {
            albumsLiveData.postValue(songUseCases.getAlbums())
        }
    }

    fun setCurrentAlbum(album: Album): Boolean {
        if (currentAlbum.value != album) {
            currentAlbum.value = album
            return true
        }
        return false
    }

    fun setPlaylist(playlist: Playlist) {
        if (playlistLiveData.value != playlist)
            playlistLiveData.value = playlist
    }

    fun shuffleCurrentPlaylist() {
        playlistLiveData.value?.apply {
            var shuffledList = originalSongs.shuffled()
            while (shuffledList == originalSongs && shuffledList.size > 1)
                shuffledList = originalSongs.shuffled()

            songs = shuffledList
            isShuffled = true
        }
        playlistLiveData.value = playlistLiveData.value
    }

    fun unShuffleCurrentPlaylist() {
        playlistLiveData.value?.apply {
            songs = originalSongs.toList()
            isShuffled = false
        }
        playlistLiveData.value = playlistLiveData.value
    }

    fun playNewSong(song: Song) {
        val currentPlay = currentPlayLiveData.value
        if (currentPlay?.id == song.id)
            return

        mediaPlayer?.reset()

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            isLoopingLiveData.value = false
        }
        mediaPlayer?.apply {
            isLooping = (isLoopingLiveData.value == true)
            setDataSource(context, song.resource)
            prepare()
            start()
        }

        currentPlayLiveData.value = song
        isPlayingLiveData.value = true
    }

    fun playFirstSongInPlaylist() {
        playlistLiveData.value?.songs?.let {
            if (it.isNotEmpty()) playNewSong(it[0])
        }
    }

    fun resumeCurrentSong() {
        if (isPlayingLiveData.value == false) {
            mediaPlayer?.start()
            isPlayingLiveData.value = true
        }
    }

    fun pauseCurrentSong() {
        if (isPlayingLiveData.value == true) {
            mediaPlayer?.pause()
            isPlayingLiveData.value = false
        }
    }

    fun playNextSong() {
        currentPlayLiveData.value?.let { currentPlay ->
            playlistLiveData.value?.let { playlist ->
                val index = playlist.songs.indexOf(currentPlay)
                playNewSong(playlist.songs[(index + 1) % playlist.songs.size])
            }
        }
    }

    fun playPreviousSong() {
        currentPlayLiveData.value?.let { currentPlay ->
            playlistLiveData.value?.let { playlist ->
                val index = playlist.songs.indexOf(currentPlay)
                playNewSong(playlist.songs[(index - 1 + playlist.songs.size) % playlist.songs.size])
            }
        }
    }

    fun observeCurrentPosition() {
        if (handler == null) {
            handler = loop(500) {
                mediaPlayer?.let {
                    currentPositionLiveData.value = it.currentPosition
                }
            }
        }
    }

    fun startLoop() {
        isLoopingLiveData.value = true
        mediaPlayer?.isLooping = true
        mediaPlayer?.setOnCompletionListener { }
    }

    fun stopLoop() {
        isLoopingLiveData.value = false
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener { playNextSong() }
    }

    fun skipNextFiveSeconds() {
        mediaPlayer?.apply {
            seekTo(min(currentPosition + 5000, duration))
        }
    }

    fun skipPreviousFiveSeconds() {
        mediaPlayer?.apply {
            seekTo(max(currentPosition - 5000, 0))
        }
    }

    fun favouriteCurrentSong() {
        currentPlayLiveData.value?.let { currentSong ->
            val isFavourite = songUseCases.favouriteSong(currentSong)
            songsLiveData.value?.first { it.id == currentSong.id }?.isFavorite = isFavourite
            getSongs()

            currentPlayLiveData.value = songsLiveData.value?.first { it.id == currentSong.id }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun clear() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        clearCurrentPlay()
        releaseMediaPlayer()
    }

    private fun clearCurrentPlay() {
        currentPlayLiveData.value = null
        isPlayingLiveData.value = false
    }

    private fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
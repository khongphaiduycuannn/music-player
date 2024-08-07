package com.notmyexample.musicplayer.presentation.ui.detail_album

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.databinding.FragmentDetailAlbumBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens.DETAIL_ALBUM
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.presentation.ui.home.SongAdapter
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PAUSE
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import com.notmyexample.musicplayer.utils.darkenColor
import com.notmyexample.musicplayer.utils.formatTime
import com.notmyexample.musicplayer.utils.getDominantColor
import com.notmyexample.musicplayer.utils.lighterColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class DetailAlbumFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val activity by lazy { requireActivity() as MainActivity }

    private val binding by lazy { FragmentDetailAlbumBinding.inflate(layoutInflater) }

    private val viewModel: DetailAlbumViewModel by viewModels()

    private val songAdapter = SongAdapter {
        setCurrentAlbum()
        playSongManager.playNewSong(it)
        activity.startPlaySongService(EVENT_PLAY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity.updateBottomBar(DETAIL_ALBUM)

        initData()
        initView()
        handleEvent()
        observeData()
    }

    private fun initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("Album", Album::class.java)
        } else {
            arguments?.getSerializable("Album") as Album
        }?.let {
            viewModel.setAlbum(it)
        }
    }

    private fun initView() {
        binding.rclSongsList.adapter = songAdapter
        binding.rclSongsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.ivBack.setOnClickListener { appNavigator.popBackStack() }

        binding.ivPlayOrPause.setOnClickListener(onPlayOrPauseButtonClick)

        binding.ivShuffle.setOnClickListener(onShuffleButtonClick)
    }

    private fun observeData() {
        viewModel.album.observe(viewLifecycleOwner) { album ->
            setAlbumDataView(album)
        }

        viewModel.songs.observe(viewLifecycleOwner) {
            songAdapter.setSongs(it.toMutableList())
        }

        with(playSongManager) {
            currentPlayLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    songAdapter.setCurrentSong(it)
                }
            }

            isPlayingLiveData.observe(viewLifecycleOwner) {
                songAdapter.setIsPlaying(it)
                setIsPlayingButtonView(it)
            }

            playlistLiveData.observe(viewLifecycleOwner) {
                setIsShuffleButtonView(it)
            }
        }
    }

    private val onPlayOrPauseButtonClick = View.OnClickListener {
        val isPlaying = playSongManager.isPlayingLiveData.value
        if (isAlbumPlaying()) {
            if (isPlaying == true) {
                playSongManager.pauseCurrentSong()
                activity.startPlaySongService(EVENT_PAUSE)
            }
            else {
                playSongManager.resumeCurrentSong()
                activity.startPlaySongService(EVENT_PLAY)
            }
        } else {
            setCurrentAlbum()
            playSongManager.playFirstSongInPlaylist()
            activity.startPlaySongService(EVENT_PLAY)
        }
    }

    private val onShuffleButtonClick = View.OnClickListener {
        val playlist = playSongManager.playlistLiveData.value
        if (playlist != null && isAlbumPlaying()) {
            if (playlist.isShuffled)
                playSongManager.unShuffleCurrentPlaylist()
            else
                playSongManager.shuffleCurrentPlaylist()
        } else {
            setCurrentAlbum()
            playSongManager.shuffleCurrentPlaylist()
            playSongManager.playFirstSongInPlaylist()
            activity.startPlaySongService(EVENT_PLAY)
        }
    }

    private fun setAlbumDataView(album: Album) {
        Glide.with(requireContext()).load(album.thumbnail).into(binding.ivThumbnail)
        album.thumbnail?.let {
            val color = getDominantColor(it)
            binding.ivTopBackground.setBackgroundColor(darkenColor(color))
            binding.ivBottomBackground.setBackgroundColor(lighterColor(color))
        }

        binding.tvName.text = album.name
        binding.tvTotalDuration.text = formatTime(album.totalDuration)

        val list = playSongManager.songsLiveData.value
        val albumSongs = list?.filter { it.albumId == album.id } ?: listOf()
        binding.tvSongs.text = "${albumSongs.map { it.name }}"

        binding.tvNumberOfSong.text = "${albumSongs.size} songs"

        viewModel.setSongs(albumSongs)
    }

    private fun setIsPlayingButtonView(isPlaying: Boolean) {
        if (isPlaying && isAlbumPlaying()) {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_pause_action_black)
        } else {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_play_action_black)
        }
    }

    private fun setIsShuffleButtonView(playlist: Playlist) {
        if (playlist.isShuffled && isAlbumPlaying()) {
            binding.ivShuffle.setImageResource(R.drawable.ic_shuffle_selected)
        }
        else {
            binding.ivShuffle.setImageResource(R.drawable.ic_shuffle)
        }
    }

    private fun setCurrentAlbum() {
        viewModel.album.value?.let { album ->
            if (playSongManager.setCurrentAlbum(album)) {
                viewModel.songs.value?.let { songs ->
                    Playlist(album.name, songs)
                }?.let { playlist ->
                    playSongManager.setPlaylist(playlist)
                }
            }
        }
    }

    private fun isAlbumPlaying(): Boolean =
        viewModel.album.value == playSongManager.currentAlbum.value
}
package com.notmyexample.musicplayer.presentation.ui.favourite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.databinding.FragmentFavouriteSongBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.presentation.ui.home.SongAdapter
import com.notmyexample.musicplayer.utils.constant.PlayEventConstant.EVENT_PAUSE
import com.notmyexample.musicplayer.utils.constant.PlayEventConstant.EVENT_PLAY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteSongFragment : Fragment() {

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val activity by lazy { requireActivity() as MainActivity }

    private val binding by lazy { FragmentFavouriteSongBinding.inflate(layoutInflater) }

    private val viewModel: FavouriteSongViewModel by viewModels()

    private val songAdapter = SongAdapter {
        setCurrentAlbum()
        playSongManager.playNewSong(it)
        activity.startPlaySongService(EVENT_PLAY)
    }

    private val sortSongBottomSheetDialog = SortSongBottomSheetDialog {
        viewModel.setSortType(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity.updateBottomBar(Screens.FAVOURITE)

        initData()
        initView()
        handleEvent()
        observeData()
    }

    private fun initData() {
        viewModel.getFavouriteSongs()
    }

    private fun initView() {
        binding.rclSongsList.adapter = songAdapter
        binding.rclSongsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.ivPlayOrPause.setOnClickListener(onPlayOrPauseButtonClick)

        binding.ivShuffle.setOnClickListener(onShuffleButtonClick)

        binding.edtSearchKey.addTextChangedListener(searchTextWatcher)

        binding.ivSort.setOnClickListener {
            sortSongBottomSheetDialog.show(activity.supportFragmentManager, "sort_dialog")
        }
    }

    private fun observeData() {
        with(viewModel) {
            favouriteSongs.observe(viewLifecycleOwner) {
                sortType.value?.let { sortType -> viewModel.sortSongs(sortType) }
            }

            sortType.observe(viewLifecycleOwner) {
                viewModel.sortSongs(it)
            }

            searchKey.observe(viewLifecycleOwner) { searchKey ->
                sortedFavouriteSong.value?.let { songs ->
                    if (searchKey.isNullOrBlank()) {
                        songAdapter.setSongs(songs.toMutableList())
                    } else {
                        songAdapter.setSongs(
                            songs.filter { it.name.contains(searchKey, true) }.toMutableList()
                        )
                    }
                }
            }

            sortedFavouriteSong.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    binding.tvNotification.visibility = View.VISIBLE
                } else {
                    binding.tvNotification.visibility = View.GONE
                    songAdapter.setSongs(it.toMutableList())
                }
            }
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
            } else {
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
        } else {
            binding.ivShuffle.setImageResource(R.drawable.ic_shuffle)
        }
    }

    private fun setCurrentAlbum() {
        val album = viewModel.album
        if (playSongManager.setCurrentAlbum(album)) {
            viewModel.favouriteSongs.value?.let { songs ->
                Playlist(album.name, songs)
            }?.let { playlist ->
                playSongManager.setPlaylist(playlist)
            }
        }
    }

    private fun isAlbumPlaying(): Boolean {
        val x = viewModel.album
        val y = playSongManager.currentAlbum.value
        return x == y
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(searchKey: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.setSearchKey(searchKey.toString())
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }
}
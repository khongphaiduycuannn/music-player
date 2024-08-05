package com.notmyexample.musicplayer.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.databinding.FragmentHomeBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens.ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.DETAIL_ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.HOME
import com.notmyexample.musicplayer.presentation.navigator.Screens.PLAY
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    private val viewModel: HomeViewModel by viewModels()

    private val albumAdapter = AlbumAdapter {
        val bundle = Bundle()
        bundle.putSerializable("Album", it)
        appNavigator.navigateTo(DETAIL_ALBUM, bundle)
    }

    private val songAdapter = SongAdapter {
        val songs = playSongManager.songsLiveData.value
        if (songs != null) {
            playSongManager.playNewSong(it)
            playSongManager.playlistLiveData.value = Playlist("All songs...", songs)
        }

        (activity as MainActivity).startPlaySongService(EVENT_PLAY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomBar(HOME)

        initData()
        initView()
        handleEvent()
        observeData()
    }

    private fun initData() {
        playSongManager.getSongs()
    }

    private fun initView() {
        binding.rclAlbumsList.adapter = albumAdapter
        binding.rclAlbumsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rclSongsList.adapter = songAdapter
        binding.rclSongsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.tvShowMoreAlbum.setOnClickListener {
            appNavigator.navigateTo(ALBUM)
        }

        binding.tvPlayAll.setOnClickListener {
            val songs = playSongManager.songsLiveData.value
            if (songs != null && songs.size > 1) {
                playSongManager.playNewSong(songs[0])
                playSongManager.playlistLiveData.value = Playlist("All songs...", songs)

                (activity as MainActivity).startPlaySongService(EVENT_PLAY)
            }
        }
    }

    private fun observeData() {
        viewModel.albums.observe(viewLifecycleOwner) {
            albumAdapter.setAlbums(it.take(5).toMutableList())
        }

        playSongManager.songsLiveData.observe(viewLifecycleOwner) {
            songAdapter.setSongs(it.toMutableList())
        }

        playSongManager.currentPlayLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                songAdapter.setCurrentSong(it)
            }
        }

        playSongManager.isPlayingLiveData.observe(viewLifecycleOwner) {
            songAdapter.setIsPlaying(it)
        }
    }
}
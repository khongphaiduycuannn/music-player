package com.notmyexample.musicplayer.presentation.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.notmyexample.musicplayer.databinding.FragmentSongsBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.presentation.ui.home.SongAdapter
import com.notmyexample.musicplayer.utils.constant.PlayEventConstant.EVENT_PLAY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongsFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val binding by lazy { FragmentSongsBinding.inflate(layoutInflater) }

    private val songAdapter = SongAdapter {
        playSongManager.playNewSong(it)
        (activity as MainActivity).startPlaySongService(EVENT_PLAY)
        appNavigator.popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomBar(Screens.SONGS)

        initView()
        handleEvent()
        observeData()
    }

    private fun initView() {
        binding.rclSongsList.adapter = songAdapter
        binding.rclSongsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.ivBack.setOnClickListener { appNavigator.popBackStack() }
    }

    private fun observeData() {
        playSongManager.playlistLiveData.observe(viewLifecycleOwner) {
            binding.tvPlaylist.text = it.name
            songAdapter.setSongs(it.songs.toMutableList())
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
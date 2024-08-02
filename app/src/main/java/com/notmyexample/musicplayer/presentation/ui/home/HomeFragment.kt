package com.notmyexample.musicplayer.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.notmyexample.musicplayer.databinding.FragmentHomeBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens.ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.DETAIL_ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.HOME
import com.notmyexample.musicplayer.presentation.navigator.Screens.SONGS
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    private val viewModel: HomeViewModel by activityViewModels()

    private val albumAdapter = AlbumAdapter {
        val bundle = Bundle()
        bundle.putSerializable("Album", it)
        appNavigator.navigateTo(DETAIL_ALBUM, bundle)
    }

    private val songAdapter = SongAdapter {
        val bundle = Bundle()
        bundle.putSerializable("Song", it)
        appNavigator.navigateTo(DETAIL_ALBUM, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomNavigationBar(HOME)

        initView()
        handleEvent()
        observeData()
    }

    private fun initView() {
        binding.rclSongsList.adapter = songAdapter
        binding.rclSongsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rclAlbumsList.adapter = albumAdapter
        binding.rclAlbumsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun handleEvent() {
        binding.tvShowMoreAlbum.setOnClickListener {
            appNavigator.navigateTo(ALBUM)
        }

        binding.tvShowMoreSong.setOnClickListener {
            appNavigator.navigateTo(SONGS)
        }
    }

    private fun observeData() {
        viewModel.songs.observe(viewLifecycleOwner) {
            songAdapter.setSongs(it.take(5).toMutableList())
        }

        viewModel.albums.observe(viewLifecycleOwner) {
            albumAdapter.setAlbums(it.take(5).toMutableList())
        }
    }
}
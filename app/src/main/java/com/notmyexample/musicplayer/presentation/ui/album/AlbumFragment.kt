package com.notmyexample.musicplayer.presentation.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.databinding.FragmentAlbumsBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens.DETAIL_ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.HOME
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val binding by lazy { FragmentAlbumsBinding.inflate(layoutInflater) }

    private val onAlbumItemClick: (Album) -> Unit = {
        val bundle = Bundle()
        bundle.putSerializable("Album", it)
        appNavigator.navigateTo(DETAIL_ALBUM, bundle)
    }

    private val getAlbumSongs: (Long) -> List<String> = { albumId ->
        val list = playSongManager.songsLiveData.value
        list?.filter { it.albumId == albumId }?.map { it.name } ?: listOf()
    }

    private val albumHomeAdapter = AlbumAdapter(
        onAlbumItemClick,
        getAlbumSongs
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomBar(HOME)

        initView()
        handleEvent()
        observeData()
    }

    private fun initView() {
        binding.rclAlbumsList.adapter = albumHomeAdapter
        binding.rclAlbumsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.ivBack.setOnClickListener {
            appNavigator.popBackStack()
        }
    }

    private fun observeData() {
        playSongManager.albumsLiveData.observe(viewLifecycleOwner) {
            albumHomeAdapter.setAlbums(it.toMutableList())
        }
    }
}
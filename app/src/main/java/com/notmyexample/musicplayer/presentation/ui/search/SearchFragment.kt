package com.notmyexample.musicplayer.presentation.ui.search

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
import com.notmyexample.musicplayer.databinding.FragmentSearchBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val binding by lazy { FragmentSearchBinding.inflate(layoutInflater) }

    private val viewModel: SearchViewModel by viewModels()

    private val searchResultAdapter = SearchResultAdapter {
        playSongManager.playNewSong(it)
        playSongManager.setPlaylist(Playlist("Selected song...", listOf(it)))
        viewModel.saveSearchResult(it)

        (activity as MainActivity).startPlaySongService(EVENT_PLAY)
    }

    private val recentSearchResultAdapter = RecentSearchAdapter {
        viewModel.deleteSearchResult(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomBar(Screens.SEARCH)

        initData()
        initView()
        handleEvent()
        observeData()
    }

    private fun initData() {
        viewModel.getLastSearchResult()
    }

    private fun initView() {
        binding.rclSearchResult.adapter = searchResultAdapter
        binding.rclSearchResult.layoutManager = LinearLayoutManager(requireContext())

        binding.rclRecentSearches.adapter = recentSearchResultAdapter
        binding.rclRecentSearches.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleEvent() {
        binding.edtSearchKey.addTextChangedListener(searchTextWatcher)
    }

    private fun observeData() {
        viewModel.lastSearchResult.observe(viewLifecycleOwner) { lastSearchesId ->
            if (lastSearchesId.isEmpty()) {
                recentSearchResultAdapter.setSongs(mutableListOf())
            } else {
                recentSearchResultAdapter.setSongs(
                    lastSearchesId.mapNotNull { searchId ->
                        val songs = playSongManager.songsLiveData.value
                        songs?.firstOrNull { it.id == searchId }
                    }.toMutableList()
                )
            }
        }

        viewModel.searchKey.observe(viewLifecycleOwner) { searchKey ->
            playSongManager.songsLiveData.value?.let { songs ->
                if (searchKey.isNullOrBlank()) {
                    searchResultAdapter.setSongs(mutableListOf())
                } else {
                    searchResultAdapter.setSongs(songs.filter { it.name.contains(searchKey, true) }
                        .toMutableList())
                }
            }
        }
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(searchKey: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.setSearchKey(searchKey.toString())
            if (!searchKey.isNullOrEmpty()) {
                binding.ivSearch.setImageResource(R.drawable.ic_close_gray)
                binding.ivSearch.setOnClickListener { binding.edtSearchKey.text = null }
            } else {
                binding.ivSearch.setImageResource(R.drawable.ic_search)
                binding.ivSearch.setOnClickListener { }
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }
}
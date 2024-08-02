package com.notmyexample.musicplayer.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.navigator.Screens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomNavigationBar(Screens.SEARCH)
    }
}
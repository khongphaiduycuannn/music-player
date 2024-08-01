package com.notmyexample.musicplayer.presentation.navigator

import androidx.fragment.app.FragmentActivity
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.ui.favourite.FavouriteSongFragment
import com.notmyexample.musicplayer.presentation.ui.home.HomeFragment
import com.notmyexample.musicplayer.presentation.ui.play.PlaySongFragment
import com.notmyexample.musicplayer.presentation.ui.search.SearchFragment
import javax.inject.Inject

enum class Screens {
    HOME,
    FAVOURITE,
    PLAY,
    SEARCH
}

interface AppNavigator {

    fun navigateTo(screen: Screens)

    fun popBackStack()

    fun popBackStack(onEmptyStack: () -> Unit)
}

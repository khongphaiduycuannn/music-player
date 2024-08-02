package com.notmyexample.musicplayer.presentation.navigator

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.ui.album.AlbumFragment
import com.notmyexample.musicplayer.presentation.ui.favourite.FavouriteSongFragment
import com.notmyexample.musicplayer.presentation.ui.home.HomeFragment
import com.notmyexample.musicplayer.presentation.ui.play.PlaySongFragment
import com.notmyexample.musicplayer.presentation.ui.search.SearchFragment
import com.notmyexample.musicplayer.presentation.ui.songs.SongsFragment
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screen: Screens) {
        val fragment = when (screen) {
            Screens.HOME -> HomeFragment()
            Screens.FAVOURITE -> FavouriteSongFragment()
            Screens.PLAY -> PlaySongFragment()
            Screens.SEARCH -> SearchFragment()
            Screens.ALBUM -> AlbumFragment()
            Screens.SONGS -> SongsFragment()
        }

        activity.supportFragmentManager.beginTransaction().apply {
            if (fromMainBackstack(screen)) {
                addToBackStack(HOME_BACKSTACK)
            } else {
                activity.supportFragmentManager.popBackStack(
                    HOME_BACKSTACK,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
            setCustomAnimations(
                R.anim.enter_zoom_in,
                R.anim.exit_zoom_out,
                R.anim.enter_zoom_out,
                R.anim.exit_zoom_in
            )
            replace(R.id.fragmentContainerView, fragment)
            setReorderingAllowed(true)
            commit()
        }
    }

    override fun popBackStack() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun popBackStack(onEmptyStack: () -> Unit) {
        activity.supportFragmentManager.apply {
            if (backStackEntryCount == 0) {
                onEmptyStack()
            } else {
                popBackStack()
            }
        }
    }

    private fun fromMainBackstack(screen: Screens): Boolean {
        return screen == Screens.ALBUM || screen == Screens.SONGS
    }

    companion object {
        const val HOME_BACKSTACK = "HOME BACKSTACK"
    }
}
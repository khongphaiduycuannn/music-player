package com.notmyexample.musicplayer.presentation.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.ui.DetailSongFragment
import com.notmyexample.musicplayer.presentation.ui.album.AlbumFragment
import com.notmyexample.musicplayer.presentation.ui.detail_album.DetailAlbumFragment
import com.notmyexample.musicplayer.presentation.ui.favourite.FavouriteSongFragment
import com.notmyexample.musicplayer.presentation.ui.home.HomeFragment
import com.notmyexample.musicplayer.presentation.ui.play.PlaySongFragment
import com.notmyexample.musicplayer.presentation.ui.search.SearchFragment
import com.notmyexample.musicplayer.presentation.ui.songs.SongsFragment
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screen: Screens) {
        val fragment = getFragment(screen)

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

    override fun navigateTo(screen: Screens, bundle: Bundle) {
        val fragment = getFragment(screen)
        fragment.arguments = bundle

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

    private fun getFragment(screen: Screens) = when (screen) {
        Screens.HOME -> HomeFragment()
        Screens.DETAIL_SONG -> DetailSongFragment()
        Screens.DETAIL_ALBUM -> DetailAlbumFragment()
        Screens.FAVOURITE -> FavouriteSongFragment()
        Screens.PLAY -> PlaySongFragment()
        Screens.SEARCH -> SearchFragment()
        Screens.ALBUM -> AlbumFragment()
        Screens.SONGS -> SongsFragment()
    }

    private fun fromMainBackstack(screen: Screens): Boolean {
        return screen == Screens.ALBUM
                || screen == Screens.SONGS
                || screen == Screens.DETAIL_ALBUM
                || screen == Screens.DETAIL_SONG
    }

    companion object {
        const val HOME_BACKSTACK = "HOME BACKSTACK"
    }
}
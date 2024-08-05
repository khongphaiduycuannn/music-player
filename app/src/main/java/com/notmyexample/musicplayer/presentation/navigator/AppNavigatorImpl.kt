package com.notmyexample.musicplayer.presentation.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.ui.album.AlbumFragment
import com.notmyexample.musicplayer.presentation.ui.detail_album.DetailAlbumFragment
import com.notmyexample.musicplayer.presentation.ui.favourite.FavouriteSongFragment
import com.notmyexample.musicplayer.presentation.ui.home.HomeFragment
import com.notmyexample.musicplayer.presentation.ui.play.PlaySongFragment
import com.notmyexample.musicplayer.presentation.ui.search.SearchFragment
import com.notmyexample.musicplayer.presentation.ui.songs.SongsFragment
import java.util.Stack
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    private val screenStack: Stack<Screens> = Stack()

    override fun getCurrentScreen(): Screens? {
        if (screenStack.size > 0)
            return screenStack.last()
        return null
    }

    override fun navigateTo(screen: Screens) {
        val fragment = getFragment(screen)
        startFragment(fragment, screen)
        screenStack.push(screen)
    }

    override fun navigateTo(screen: Screens, bundle: Bundle) {
        val fragment = getFragment(screen)
        fragment.arguments = bundle
        startFragment(fragment, screen)
        screenStack.push(screen)
    }

    override fun popBackStack() {
        activity.supportFragmentManager.popBackStack()
        screenStack.pop()
    }

    override fun popBackStack(onEmptyStack: () -> Unit) {
        activity.supportFragmentManager.apply {
            if (backStackEntryCount == 0) {
                onEmptyStack()
            } else {
                popBackStack()
                screenStack.pop()
            }
        }
    }

    private fun getFragment(screen: Screens) = when (screen) {
        Screens.HOME -> HomeFragment()
        Screens.DETAIL_ALBUM -> DetailAlbumFragment()
        Screens.FAVOURITE -> FavouriteSongFragment()
        Screens.PLAY -> PlaySongFragment()
        Screens.SONGS -> SongsFragment()
        Screens.SEARCH -> SearchFragment()
        Screens.ALBUM -> AlbumFragment()
    }

    private fun startFragment(fragment: Fragment, screen: Screens) {
        activity.supportFragmentManager.beginTransaction().apply {
            if (fromMainBackstack(screen)) {
                addToBackStack(HOME_BACKSTACK)
            } else if (fromPlayBackstack(screen)) {
                addToBackStack(PLAY_BACKSTACK)
            } else {
                activity.supportFragmentManager.popBackStack(
                    HOME_BACKSTACK,
                    POP_BACK_STACK_INCLUSIVE
                )
                activity.supportFragmentManager.popBackStack(
                    HOME_BACKSTACK,
                    POP_BACK_STACK_INCLUSIVE
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

    private fun fromMainBackstack(screen: Screens): Boolean {
        return screen == Screens.ALBUM
                || screen == Screens.DETAIL_ALBUM
    }

    private fun fromPlayBackstack(screen: Screens): Boolean {
        return screen == Screens.PLAY
                || screen == Screens.SONGS
    }

    companion object {
        const val HOME_BACKSTACK = "HOME BACKSTACK"
        const val PLAY_BACKSTACK = "PLAY BACKSTACK"
    }
}
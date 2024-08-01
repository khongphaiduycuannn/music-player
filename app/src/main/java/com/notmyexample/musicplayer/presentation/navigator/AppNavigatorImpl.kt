package com.notmyexample.musicplayer.presentation.navigator

import androidx.fragment.app.FragmentActivity
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.ui.favourite.FavouriteSongFragment
import com.notmyexample.musicplayer.presentation.ui.home.HomeFragment
import com.notmyexample.musicplayer.presentation.ui.play.PlaySongFragment
import com.notmyexample.musicplayer.presentation.ui.search.SearchFragment
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screen: Screens) {
        val fragment = when (screen) {
            Screens.HOME -> HomeFragment()
            Screens.FAVOURITE -> FavouriteSongFragment()
            Screens.PLAY -> PlaySongFragment()
            Screens.SEARCH -> SearchFragment()
        }

        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_zoom_in,
                R.anim.exit_zoom_out,
                R.anim.enter_zoom_out,
                R.anim.exit_zoom_in
            )
            .replace(R.id.fragmentContainerView, fragment)
//            .addToBackStack(fragment::class.java.canonicalName)
            .setReorderingAllowed(true)
            .commit()
    }

    override fun popBackStack() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun popBackStack(onEmptyStack: () -> Unit) {
        activity.supportFragmentManager.apply {
            if (backStackEntryCount <= 1) {
                onEmptyStack()
            } else {
                popBackStack()
            }
        }
    }
}
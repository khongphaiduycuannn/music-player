package com.notmyexample.musicplayer.presentation.navigator

import android.os.Bundle

enum class Screens {
    HOME,
    FAVOURITE,
    DETAIL_ALBUM,
    PLAY,
    SONGS,
    SEARCH,
    ALBUM
}

interface AppNavigator {

    fun getCurrentScreen(): Screens?

    fun navigateTo(screen: Screens)

    fun navigateTo(screen: Screens, bundle: Bundle)

    fun navigateTo(screen: Screens, enter: Int, exit: Int, popEnter: Int, popExit: Int)

    fun popBackStack()

    fun popBackStack(onEmptyStack: () -> Unit)
}

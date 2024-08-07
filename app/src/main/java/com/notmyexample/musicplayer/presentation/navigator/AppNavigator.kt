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

    fun popBackStack()

    fun popBackStack(onEmptyStack: () -> Unit)
}

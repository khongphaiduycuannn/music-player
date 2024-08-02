package com.notmyexample.musicplayer.presentation.navigator

import android.os.Bundle

enum class Screens {
    HOME,
    FAVOURITE,
    DETAIL_ALBUM,
    DETAIL_SONG,
    PLAY,
    SEARCH,
    ALBUM,
    SONGS,
}

interface AppNavigator {

    fun navigateTo(screen: Screens)

    fun navigateTo(screen: Screens, bundle: Bundle)

    fun popBackStack()

    fun popBackStack(onEmptyStack: () -> Unit)
}

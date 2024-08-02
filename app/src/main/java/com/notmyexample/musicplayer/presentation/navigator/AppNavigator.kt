package com.notmyexample.musicplayer.presentation.navigator

enum class Screens {
    HOME,
    FAVOURITE,
    PLAY,
    SEARCH,
    ALBUM,
    SONGS,
}

interface AppNavigator {

    fun navigateTo(screen: Screens)

    fun popBackStack()

    fun popBackStack(onEmptyStack: () -> Unit)
}

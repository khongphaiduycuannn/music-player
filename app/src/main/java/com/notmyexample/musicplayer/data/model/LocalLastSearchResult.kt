package com.notmyexample.musicplayer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalLastSearchResult(
    val data: MutableList<Song>
) : Parcelable
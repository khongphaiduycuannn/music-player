package com.notmyexample.musicplayer.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Song(
    val id: Long,
    val name: String = "",
    val author: String? = null,
    val duration: Int = 0,
    val thumbnail: Bitmap?,
    val resource: Uri,
    var isFavorite: Boolean = false,
    val albumId: Long
) : Serializable, Parcelable

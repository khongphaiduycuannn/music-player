package com.notmyexample.musicplayer.data.model

import android.graphics.Bitmap
import android.net.Uri
import java.io.Serializable

data class Song(
    val id: Long,
    val name: String? = null,
    val author: String? = null,
    val duration: Int = 0,
    val thumbnail: Bitmap?,
    val resource: Uri,
    var isFavorite: Boolean = false,
    val albumId: Long
) : Serializable

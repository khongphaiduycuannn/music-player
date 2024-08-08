package com.notmyexample.musicplayer.data.model

import android.graphics.Bitmap
import java.io.Serializable

data class Album(
    val id: Long,
    val name: String? = null,
    val thumbnail: Bitmap? = null,
    var totalDuration: Int = 0,
) : Serializable

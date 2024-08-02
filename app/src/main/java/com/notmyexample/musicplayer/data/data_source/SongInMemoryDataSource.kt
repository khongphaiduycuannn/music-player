package com.notmyexample.musicplayer.data.data_source

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.Song

class SongInMemoryDataSource(
    val context: Context
) : SongDataSource {

    override suspend fun getSongs(): List<Song> {
        return listOf(
            Song(1, "Mèo hoang", "Ngọt", 250000, defaultThumbnailList[0], Uri.EMPTY, false, 1),
            Song(2, "Mai mình xa", "Thịnh Suy", 250000, defaultThumbnailList[1], Uri.EMPTY, false, 2),
            Song(3, "Phút ban đầu", "Vũ.", 250000, defaultThumbnailList[2], Uri.EMPTY, false, 3),
        )
    }

    override suspend fun getAlbums(): List<Album> {
        return listOf(
            Album(1, "Tuyển tập nhạc Ngot sôi động 2019", "Ngot", defaultThumbnailList[3], 1),
            Album(2, "Bài hát hay nhất của Thịnh Suy", "Thịnh Suy", defaultThumbnailList[4], 1),
            Album(3, "Bài hát hay nhất của Vũ", "Vũ.", defaultThumbnailList[5], 1)
        )
    }

    private val defaultThumbnailList by lazy {
        arrayOf(
            loadThumbnail(R.drawable.img_thumbnail_1),
            loadThumbnail(R.drawable.img_thumbnail_2),
            loadThumbnail(R.drawable.img_thumbnail_3),
            loadThumbnail(R.drawable.img_thumbnail_4),
            loadThumbnail(R.drawable.img_thumbnail_5),
            loadThumbnail(R.drawable.img_thumbnail_6)
        )
    }

    private fun loadThumbnail(thumbnail: Int) = Glide
        .with(context)
        .asBitmap()
        .load(thumbnail)
        .submit(512, 512)
        .get()
}
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

    private val songs by lazy {
        listOf(
            Song(
                1,
                "Mèo hoang",
                "Ngọt",
                301349,
                defaultThumbnailList[0],
                Uri.parse("content://media/external/audio/media/1000000041"),
                false,
                1
            ),
            Song(
                2,
                "Người gieo mầm xanh",
                "Hoàng Dũng",
                240562,
                defaultThumbnailList[1],
                Uri.parse("content://media/external/audio/media/1000000043"),
                false,
                2
            ),
            Song(
                3,
                "Mai mình xa",
                "Thịnh Suy",
                189362,
                defaultThumbnailList[2],
                Uri.parse("content://media/external/audio/media/1000000040"),
                false,
                3
            ),
        )
    }

    override suspend fun getSongs(): List<Song> {
        return songs
    }

    override suspend fun getAlbums(): List<Album> {
        return listOf(
            Album(1, "Tuyển tập nhạc Ngot sôi động 2019", "Ngot", defaultThumbnailList[3], 1),
            Album(2, "Bài hát hay nhất của Thịnh Suy", "Thịnh Suy", defaultThumbnailList[4], 1),
            Album(3, "Bài hát hay nhất của Vũ", "Vũ.", defaultThumbnailList[5], 1)
        )
    }

    override fun favouriteSong(song: Song): Boolean {
        val result = songs.first { it.id == song.id }
        result.isFavorite = !result.isFavorite
        return result.isFavorite
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
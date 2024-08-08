package com.notmyexample.musicplayer.data.data_source

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.LocalLastSearchResult
import com.notmyexample.musicplayer.data.model.Song
import com.tencent.mmkv.MMKV

class SongInMemoryDataSource(
    val context: Context,
    private val kv: MMKV
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
                1
            ),
        )
    }

    override suspend fun getSongs(): List<Song> {
        return songs
    }

    override suspend fun getFavouriteSongs(): List<Song> {
        return songs.filter { it.isFavorite }
    }

    override suspend fun getAlbums(): List<Album> {
        val result = listOf(
            Album(1, "Tuyển tập nhạc Ngot sôi động 2019", defaultThumbnailList[3], 0),
            Album(2, "Bài hát hay nhất của Thịnh Suy", defaultThumbnailList[4], 0),
            Album(3, "Bài hát hay nhất của Vũ", defaultThumbnailList[5], 0)
        )
        songs.forEach { result[(it.albumId - 1).toInt()].totalDuration += it.duration }
        return result
    }

    override fun favouriteSong(song: Song): Boolean {
        val result = songs.first { it.id == song.id }
        result.isFavorite = !result.isFavorite
        return result.isFavorite
    }

    override fun saveSearchResult(song: Song) {
        var localLastSearchResult =
            kv.decodeParcelable(LAST_SEARCH_RESULT, LocalLastSearchResult::class.java)
        if (localLastSearchResult == null)
            localLastSearchResult = LocalLastSearchResult(mutableListOf())

        localLastSearchResult.data.forEach {
            if (song.id == it) return
        }

        if (localLastSearchResult.data.size < 3) {
            localLastSearchResult.data += song.id
        } else {
            localLastSearchResult.data[0] = localLastSearchResult.data[1]
            localLastSearchResult.data[1] = localLastSearchResult.data[2]
            localLastSearchResult.data[2] = song.id
        }

        kv.encode(LAST_SEARCH_RESULT, localLastSearchResult)
    }

    override fun getLastSearchResult(): List<Long> {
        return kv.decodeParcelable(LAST_SEARCH_RESULT, LocalLastSearchResult::class.java)?.data
            ?: listOf()
    }

    override fun deleteSearchResult(song: Song) {
        val localLastSearchResult =
            kv.decodeParcelable(LAST_SEARCH_RESULT, LocalLastSearchResult::class.java)
        localLastSearchResult?.data?.remove(song.id)
        kv.encode(LAST_SEARCH_RESULT, localLastSearchResult)
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

    companion object {
        const val LAST_SEARCH_RESULT = "last_search_result"
    }
}
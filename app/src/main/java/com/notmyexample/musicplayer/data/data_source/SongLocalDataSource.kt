package com.notmyexample.musicplayer.data.data_source

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.data_source.SongInMemoryDataSource.Companion.LAST_SEARCH_RESULT
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.data.model.LocalLastSearchResult
import com.notmyexample.musicplayer.data.model.Song
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SongLocalDataSource"

class SongLocalDataSource(
    val context: Context,
    private val kv: MMKV
) : SongDataSource {

    private var songs = listOf<Song>()

    private val mediaCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

    private val albumCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        }

    private val mediaProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.IS_FAVORITE,
        MediaStore.Audio.Media.ALBUM_ID
    )

    private val albumProjection = arrayOf(
        MediaStore.Audio.Albums.ALBUM_ID,
        MediaStore.Audio.Albums.ALBUM,
    )

    private fun getAllSongSelection(): String {
        val durationColumn = MediaStore.Audio.Media.DURATION
        return "${getMediaType()} LIKE '%mp3' AND $durationColumn > 1000 " +
                "OR ${getMediaType()} LIKE '%m4a' AND $durationColumn > 1000"
    }

    private fun getFavouriteSongSelection(): String {
        val durationColumn = MediaStore.Audio.Media.DURATION
        val favouriteColumn = MediaStore.Audio.Media.IS_FAVORITE
        return "${getMediaType()} LIKE '%mp3' AND $favouriteColumn = 1 AND $durationColumn > 1000 " +
                "OR ${getMediaType()} LIKE '%m4a' AND $favouriteColumn = 1 AND $durationColumn > 1000"
    }

    private fun getSongByIdSelection(id: Long): String {
        val idColumn = MediaStore.Audio.Media._ID
        return "$idColumn = $id"
    }

    override suspend fun getSongs(): List<Song> {
        return withContext(Dispatchers.IO) {
            val contentResolver = context.contentResolver

            val query = contentResolver.query(
                mediaCollection,
                mediaProjection,
                getAllSongSelection(),
                null,
                null
            )
            songs = runMediaCursor(query)
            songs
        }
    }

    override suspend fun getFavouriteSongs(): List<Song> {
        return withContext(Dispatchers.IO) {
            val contentResolver = context.contentResolver

            val query = contentResolver.query(
                mediaCollection,
                mediaProjection,
                getFavouriteSongSelection(),
                null,
                null
            )
            runMediaCursor(query)
        }
    }

    override suspend fun getAlbums(): List<Album> {
        return withContext(Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val query = contentResolver.query(
                albumCollection,
                albumProjection,
                null,
                null,
                null
            )
            getSongs()
            runAlbumCursor(query)
        }
    }

    override fun favouriteSong(song: Song): Boolean {
//        val contentResolver = context.contentResolver
//        val updatedSongDetails = ContentValues().apply {
//            put(MediaStore.Audio.Media.IS_FAVORITE, "1")
//        }
//
//        contentResolver.update(
//            song.resource,
//            updatedSongDetails,
//            getSongByIdSelection(song.id),
//            null
//        )
        return !song.isFavorite
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

    private fun runMediaCursor(query: Cursor?): List<Song> {
        val resultList = mutableListOf<Song>()
        query?.use { cursor ->
            val idColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val nameColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val authorColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val isFavouriteColumnIndex =
                cursor.getColumnIndex(MediaStore.Audio.Media.IS_FAVORITE)
            val albumIdColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumnIndex)
                val name = cursor.getString(nameColumnIndex)
                val author = cursor.getString(authorColumnIndex)
                val duration = cursor.getInt(durationColumnIndex)
                val resource =
                    ContentUris.withAppendedId(mediaCollection, id)
                var thumbnail: Bitmap? = loadThumbnail(resource)
                if (thumbnail == null) {
                    thumbnail = defaultThumbnailList[resultList.size % 6]
                }
                val isFavourite = (cursor.getInt(isFavouriteColumnIndex) == 1)
                val albumId = cursor.getLong(albumIdColumnIndex)

                resultList += Song(
                    id,
                    name,
                    author,
                    duration,
                    thumbnail,
                    resource,
                    isFavourite,
                    albumId
                )
            }
        }
        return resultList.toList()
    }

    private fun runAlbumCursor(query: Cursor?): List<Album> {
        val resultList = mutableListOf<Album>()
        query?.use { cursor ->
            val albumIdColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)
            val nameColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(albumIdColumnIndex)
                val name = cursor.getString(nameColumnIndex)
                val albumSongs = songs.filter { it.albumId == id }

                if (albumSongs.isNotEmpty() && resultList.find { it.id == id } == null) {
                    val thumbnail = albumSongs[0].thumbnail
                    val totalDuration = albumSongs.sumOf { it.duration }
                    resultList += Album(id, name, thumbnail, totalDuration)
                }
            }
        }
        return resultList
    }

    private fun getMediaType(): String {
        return MediaStore.Audio.Media.DISPLAY_NAME.split(".").last()
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

    private fun loadThumbnail(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val data = retriever.embeddedPicture
            retriever.release()
            if (data == null) {
                return null
            }
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        } catch (e: Exception) {
            return null
        }
    }
}
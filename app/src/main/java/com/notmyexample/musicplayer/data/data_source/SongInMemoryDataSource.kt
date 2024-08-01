package com.notmyexample.musicplayer.data.data_source

import android.net.Uri
import com.notmyexample.musicplayer.data.model.Song

class SongInMemoryDataSource : SongDataSource {

    override fun getSongs(): List<Song> {
        return listOf(
            Song(1, "Mèo hoang", "Ngọt", 250000, null, Uri.EMPTY),
            Song(2, "Mai mình xa", "Thịnh Suy", 250000, null, Uri.EMPTY),
            Song(3, "Phút đầu", "Vũ.", 250000, null, Uri.EMPTY),
        )
    }
}
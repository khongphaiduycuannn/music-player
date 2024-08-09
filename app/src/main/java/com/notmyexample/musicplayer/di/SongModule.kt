package com.notmyexample.musicplayer.di

import android.content.Context
import com.notmyexample.musicplayer.data.data_source.SongDataSource
import com.notmyexample.musicplayer.data.data_source.SongLocalDataSource
import com.notmyexample.musicplayer.data.repository.SongRepository
import com.notmyexample.musicplayer.use_case.song.DeleteSearchResult
import com.notmyexample.musicplayer.use_case.song.FavouriteSong
import com.notmyexample.musicplayer.use_case.song.GetAlbums
import com.notmyexample.musicplayer.use_case.song.GetFavouriteSongs
import com.notmyexample.musicplayer.use_case.song.GetLastSearchResult
import com.notmyexample.musicplayer.use_case.song.GetSongs
import com.notmyexample.musicplayer.use_case.song.SaveSearchResult
import com.notmyexample.musicplayer.use_case.song.SongUseCases
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SongModule {

    @Provides
    @Singleton
    fun provideDataSource(
        @ApplicationContext context: Context,
        kv: MMKV
    ): SongDataSource {
//        return SongInMemoryDataSource(context, kv)
        return SongLocalDataSource(context, kv)
    }

    @Provides
    @Singleton
    fun provideSongRepository(songDataSource: SongDataSource): SongRepository {
        return SongRepository(songDataSource)
    }

    @Provides
    @Singleton
    fun provideSongUseCases(songRepository: SongRepository): SongUseCases {
        return SongUseCases(
            getSongs = GetSongs(songRepository),
            getAlbums = GetAlbums(songRepository),
            favouriteSong = FavouriteSong(songRepository),
            saveSearchResult = SaveSearchResult(songRepository),
            getLastSearchResult = GetLastSearchResult(songRepository),
            deleteSearchResult = DeleteSearchResult(songRepository),
            getFavouriteSongs = GetFavouriteSongs(songRepository)
        )
    }
}
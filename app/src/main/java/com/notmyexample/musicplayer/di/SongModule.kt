package com.notmyexample.musicplayer.di

import android.content.Context
import com.notmyexample.musicplayer.data.data_source.SongDataSource
import com.notmyexample.musicplayer.data.data_source.SongInMemoryDataSource
import com.notmyexample.musicplayer.data.repository.SongRepository
import com.notmyexample.musicplayer.use_case.song.GetAlbums
import com.notmyexample.musicplayer.use_case.song.GetSongs
import com.notmyexample.musicplayer.use_case.song.SongUseCases
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
    fun provideDataSource(@ApplicationContext context: Context): SongDataSource {
        return SongInMemoryDataSource(context)
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
            getAlbums = GetAlbums(songRepository)
        )
    }
}
package com.notmyexample.musicplayer

import android.app.Application
import com.notmyexample.musicplayer.presentation.service.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MusicApplication : Application() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager.createChannelNotification()
    }
}
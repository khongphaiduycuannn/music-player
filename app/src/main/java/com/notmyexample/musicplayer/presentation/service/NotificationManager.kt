package com.notmyexample.musicplayer.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(@ApplicationContext val context: Context) {

    fun createChannelNotification() {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setSound(null, null)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        channel.setSound(null, null)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "channel_music_play"
        const val CHANNEL_NAME = "Music play channel"
    }
}
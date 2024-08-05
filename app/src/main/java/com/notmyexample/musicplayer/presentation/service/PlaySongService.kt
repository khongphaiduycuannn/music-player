package com.notmyexample.musicplayer.presentation.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.service.NotificationManager.Companion.CHANNEL_ID
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_CLOSE
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_NAME
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_NEXT_MUSIC
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PAUSE
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PREVIOUS_MUSIC
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "PlaySongService"

@AndroidEntryPoint
class PlaySongService : Service() {

    @Inject
    lateinit var playSongManager: PlaySongManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        playSongManager.observeCurrentPosition()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra(EVENT_NAME, 0)?.let {
            handleEvent(it)
            sendNotification(it)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playSongManager.clear()
    }

    private fun handleEvent(event: Int) {
        when (event) {
            EVENT_PLAY -> playSongManager.resumeCurrentSong()
            EVENT_PAUSE -> playSongManager.pauseCurrentSong()
            EVENT_NEXT_MUSIC -> playSongManager.playNextSong()
            EVENT_PREVIOUS_MUSIC -> playSongManager.playPreviousSong()
            EVENT_CLOSE -> stopSelf()
        }
    }

    private fun sendNotification(event: Int) {
        playSongManager.currentPlayLiveData.value?.let { currentPlay ->
            val mediaSession = MediaSessionCompat(this, TAG)
            val notification = NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle(currentPlay.name)
                .setContentText(currentPlay.author)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_note)
                .setLargeIcon(currentPlay.thumbnail)
                .setContentIntent(getContentPendingIntent())
                .addAction(
                    R.drawable.ic_previous,
                    "Previous",
                    getActionPendingIntent(EVENT_PREVIOUS_MUSIC)
                )
                .addAction(
                    if (event == EVENT_PAUSE || event == EVENT_CLOSE)
                        R.drawable.ic_play_action
                    else R.drawable.ic_pause_action,
                    "Pause",
                    if (event == EVENT_PAUSE || event == EVENT_CLOSE)
                        getActionPendingIntent(EVENT_PLAY)
                    else getActionPendingIntent(EVENT_PAUSE)
                )
                .addAction(
                    R.drawable.ic_next,
                    "Next",
                    getActionPendingIntent(EVENT_NEXT_MUSIC)
                )
                .addAction(
                    R.drawable.ic_close,
                    "Close",
                    getActionPendingIntent(EVENT_CLOSE)
                )
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2, 3)
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setSound(null)
                .build()

            startForeground(1, notification)
            mediaSession.release()
        }
    }

    private fun getContentPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun getActionPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, PlaySongService::class.java)
        intent.putExtra(EVENT_NAME, action)

        return PendingIntent.getService(
            applicationContext,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
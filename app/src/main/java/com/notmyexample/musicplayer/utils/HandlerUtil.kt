package com.notmyexample.musicplayer.utils

import android.os.Handler
import android.os.Looper

fun loop(delay: Long, task: () -> Unit): Handler {
    val handler = Handler(Looper.getMainLooper())
    handler.post(object : Runnable {
        override fun run() {
            task()
            handler.postDelayed(this, delay)
        }
    })
    return handler
}

fun delay(delay: Long, task: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        task()
    }, delay)
}
package com.notmyexample.musicplayer.utils

import android.app.Activity

fun Activity.requestPermission(permission: String) {
    requestPermissions(arrayOf(permission), 1)
}

fun Activity.requestPermissions(permissions: Array<String>) {
    requestPermissions(permissions, 1)
}
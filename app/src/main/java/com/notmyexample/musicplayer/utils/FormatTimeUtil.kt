package com.notmyexample.musicplayer.utils

fun formatTime(time: Int): String {
    var hh = "${time / 60000}"
    while (hh.length < 2) hh = "0$hh"
    var mm = "${time / 1000 % 60}"
    while (mm.length < 2) mm = "0$mm"
    return "$hh:$mm"
}
package com.notmyexample.musicplayer.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt

fun getDominantColor(bitmap: Bitmap): Int {
    val newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
    val color = newBitmap.getPixel(0, 0)
    newBitmap.recycle()
    return color
}

@ColorInt
fun darkenColor(@ColorInt color: Int): Int {
    return Color.HSVToColor(FloatArray(3).apply {
        Color.colorToHSV(color, this)
        this[2] *= 0.8f
    })
}

@ColorInt
fun lighterColor(@ColorInt color: Int): Int {
    return Color.HSVToColor(FloatArray(3).apply {
        Color.colorToHSV(color, this)
        this[2] *= 1.2f
    })
}
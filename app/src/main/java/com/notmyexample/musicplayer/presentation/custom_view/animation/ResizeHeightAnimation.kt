package com.notmyexample.musicplayer.presentation.custom_view.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeHeightAnimation(
    private val view: View,
    private val fromHeight: Float,
    private val toHeight: Float,
    private val isHide: Boolean,
    duration: Long
) : Animation() {

    init {
        this.duration = duration
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation?
    ) {
        val height = (toHeight - fromHeight) * interpolatedTime + fromHeight
        val layoutParams = view.layoutParams
        layoutParams.height = height.toInt()
        view.requestLayout()

        var alpha = 1f * interpolatedTime
        if (isHide) alpha = 1 - alpha
        view.alpha = alpha
    }
}
package com.klymchuk.githubrepos.utils

import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Slide

fun Fragment.applySlideTransitions() {
    val transitionDuration = 250L
    exitTransition = Slide(Gravity.START).apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    reenterTransition = Slide(Gravity.START).apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    enterTransition = Slide(Gravity.END).apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    returnTransition = Slide(Gravity.END).apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
}

fun Fragment.applyFadeTransitions() {
    val transitionDuration = 250L
    exitTransition = Fade().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    reenterTransition = Fade().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    enterTransition = Fade().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
    returnTransition = Fade().apply {
        interpolator = AccelerateDecelerateInterpolator()
        duration = transitionDuration
    }
}
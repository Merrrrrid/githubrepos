package com.klymchuk.githubrepos.utils

import android.util.Log
import com.klymchuk.githubrepos.BuildConfig

object Logger {
    private const val TAG = "githubrepos.mylog"

    fun d(tag: String, message: String) {
        if (!BuildConfig.DEBUG) return

        Log.d(TAG, "$tag $message")
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        if (!BuildConfig.DEBUG) return

        Log.e(TAG, "$tag $message", throwable)
    }
}
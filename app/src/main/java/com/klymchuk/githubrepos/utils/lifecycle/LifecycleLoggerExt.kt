package com.klymchuk.githubrepos.utils.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.klymchuk.githubrepos.utils.Logger

private const val TAG = "_lifecycle"

fun Application.addLifecycleLogger() {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            kotlin.runCatching {
                if (activity is AppCompatActivity) {
                    activity.supportFragmentManager.addLifecycleLogger()
                }
            }
        }

        override fun onActivityResumed(activity: Activity) {
            Logger.d(TAG, "[ON_RESUME] ${activity.javaClass.simpleName}")
        }

        override fun onActivityPaused(activity: Activity) {
            Logger.d(TAG, "[ON_PAUSE]  ${activity.javaClass.simpleName}")
        }
    })
}

fun FragmentManager.addLifecycleLogger() {
    registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            Logger.d(TAG, "[ON_RESUME] ${fragment.javaClass.simpleName}")
        }

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
            Logger.d(TAG, "[ON_PAUSE]  ${fragment.javaClass.simpleName}")
        }
    }, true)
}
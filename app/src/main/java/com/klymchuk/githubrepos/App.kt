package com.klymchuk.githubrepos

import android.app.Application
import com.klymchuk.githubrepos.di.AppComponent
import com.klymchuk.githubrepos.di.DaggerAppComponent
import com.klymchuk.githubrepos.utils.lifecycle.addLifecycleLogger

class App : Application() {

    var component: AppComponent? = null
        private set

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        component = DaggerAppComponent.builder().also {
            it.context(this)
        }.build()

        addLifecycleLogger()
    }
}
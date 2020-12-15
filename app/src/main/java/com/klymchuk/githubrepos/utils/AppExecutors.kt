package com.klymchuk.githubrepos.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppExecutors @Inject constructor() {
    val diskExecutor: Executor = Executors.newSingleThreadExecutor()
}

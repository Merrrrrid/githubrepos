package com.klymchuk.githubrepos.utils

object Reporter {
    /**
     * User actions! e.g. click on button, scroll list etc.
     */
    fun userAction(where: String, description: String) {
        Logger.d(where, "[USER_ACTION] $description")
    }

    /**
     * App logic. e.g. making request, receiving response etc.
     */
    fun appAction(where: String, description: String) {
        Logger.d(where, "[APP_ACTION] $description")
    }
}
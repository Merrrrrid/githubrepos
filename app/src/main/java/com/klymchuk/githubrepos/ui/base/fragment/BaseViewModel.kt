package com.klymchuk.githubrepos.ui.base.fragment

import androidx.lifecycle.ViewModel
import com.klymchuk.githubrepos.utils.Logger
import com.klymchuk.githubrepos.utils.logTag

open class BaseViewModel : ViewModel() {

    private val logTag = logTag()

    init {
        Logger.d(logTag, "[VM] init hash: ${hashCode()}")
    }

    override fun onCleared() {
        Logger.d(logTag, "[VM] onCleared hash: ${hashCode()}")
        super.onCleared()
    }
}
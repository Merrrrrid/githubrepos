package com.klymchuk.githubrepos.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.base.commands.ViewCommandProcessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewCommandProcessor @Inject constructor() : ViewCommandProcessor<MainActivity>()

@Singleton
class MainViewModel @Inject constructor(
    private val mCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    data class State(
        val isProgress: Boolean = false,
    )

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    fun observeCommands(owner: LifecycleOwner, view: MainActivity) {
        mCommands.observe(owner, view)
    }

    init {
        setId("Main")
        mState = MutableLiveData(State())
    }

}
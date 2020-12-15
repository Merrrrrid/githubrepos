package com.klymchuk.githubrepos.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klymchuk.githubrepos.navigation.Destinations
import com.klymchuk.githubrepos.navigation.Router
import com.klymchuk.githubrepos.navigation.modifierFade
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.base.commands.ViewCommandProcessor
import javax.inject.Inject
import javax.inject.Singleton
import com.klymchuk.githubrepos.utils.logTag

@Singleton
class MainViewCommandProcessor @Inject constructor() : ViewCommandProcessor<MainActivity>()

@Singleton
class MainViewModel @Inject constructor(
    private val mRouter: Router,
    private val mCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    private val logTag = logTag()

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

    //==============================================================================================
    // *** View ***
    //==============================================================================================


    fun navigateToRepos(){
        mRouter.replace(Destinations.repos(), modifierFade())
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

}
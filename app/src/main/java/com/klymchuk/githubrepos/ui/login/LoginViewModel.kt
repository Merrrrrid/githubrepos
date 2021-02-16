package com.klymchuk.githubrepos.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel

class LoginViewModel @ViewModelInject constructor(
    private val mMainCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    fun onLoginButtonClicked(){
        mMainCommands.enqueue { it.navigateToGitHubLogin() }
    }

}
package com.klymchuk.githubrepos.ui.main.login

import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val mMainCommands: MainViewCommandProcessor,
) : BaseViewModel() {

}
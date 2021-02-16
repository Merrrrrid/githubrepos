package com.klymchuk.githubrepos.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.klymchuk.githubrepos.data.db.entity.User
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashViewModel @ViewModelInject constructor(
    private val mDatabaseRepository: DatabaseRepository,
) : BaseViewModel() {

    private val logTag = logTag()

    data class State(
        val userFromDB: User? = null,
    )

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    init {
        mState = MutableLiveData(State())
    }

    fun checkUserInDBOnStart() {
        Reporter.appAction(logTag, "navigateToMainMenu")

        val oldState = mState.value!!
        viewModelScope.launch(Dispatchers.IO) {
            delay(1500)
            val user = mDatabaseRepository.getUser()
            withContext(Dispatchers.Main) {
                if (user != null)
                    mState.value = oldState.copy(userFromDB = user)
                else
                    mState.value = oldState.copy(userFromDB = User())
            }
        }
    }
}
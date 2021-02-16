package com.klymchuk.githubrepos.ui

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.klymchuk.githubrepos.data.db.entity.User
import com.klymchuk.githubrepos.data.network.model.AccessToken
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.data.repositories.NetworkRepository
import com.klymchuk.githubrepos.ui.base.commands.ViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewCommandProcessor @Inject constructor() : ViewCommandProcessor<MainActivity>()

@Singleton
class MainViewModel @Inject constructor(
    private val mDatabaseRepository: DatabaseRepository,
    private val mNetworkRepository: NetworkRepository,
    private val mCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    private val logTag = logTag()

    data class State(
        val isProgress: Boolean = false,
        val token: String = "",
        val errorMessage: String = "",
        val userFromDB: User? = null,
    )

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    fun observeCommands(owner: LifecycleOwner, view: MainActivity) {
        mCommands.observe(owner, view)
    }

    init {
        mState = MutableLiveData(State())
    }

    fun onCodeRetrieve(code: String) {
        Reporter.appAction(logTag, "onCodeRetrieve")
        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        Reporter.appAction(logTag, "getAccessToken")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)

        viewModelScope.launch(Dispatchers.IO) {

            Log.e("asd", code.toString())

            val accessToken: AccessToken = mNetworkRepository.getAccessToken(code)

            withContext(Dispatchers.Main) {
                mState.value = oldState.copy(token = accessToken.accessToken)
            }

        }
    }

    fun onCodeErrorRetrieve(error: String) {
        Reporter.appAction(logTag, "onCodeErrorRetrieve")

        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = error)
    }

    fun checkCodeOnResume() {
        mCommands.enqueue { it.checkIsCodeExist() }
    }

    fun saveUserToken() {
        Reporter.appAction(logTag, "saveUserToken")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            mDatabaseRepository.insertUser(User(token = oldState.token))
            withContext(Dispatchers.Main) {
                mState.value = oldState.copy(isProgress = false)
                mCommands.enqueue { it.navigateToRepos() }
            }
        }
    }

}
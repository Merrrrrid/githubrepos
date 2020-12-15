package com.klymchuk.githubrepos.ui.login

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klymchuk.githubrepos.data.db.model.User
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.data.repositories.NetworkRepository
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.base.commands.ViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.utils.Reporter
import javax.inject.Inject
import javax.inject.Singleton
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@Singleton
class MainViewCommandProcessor @Inject constructor() : ViewCommandProcessor<LoginActivity>()

@Singleton
class LoginActivityViewModel @Inject constructor(
    private val mDatabaseRepository: DatabaseRepository,
    private val mNetworkRepository: NetworkRepository,
    private val mCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    private val logTag = logTag()

    data class State(
        val isProgress: Boolean = false,
        val token: String = "",
        val errorMessage: String = "",
    )

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    fun observeCommands(owner: LifecycleOwner, view: LoginActivity) {
        mCommands.observe(owner, view)
    }

    init {
        setId("Main")
        mState = MutableLiveData(State())
    }

    fun onLoginButtonClicked(){
        Reporter.userAction(logTag, "onLoginButtonClicked")

        mCommands.enqueue { it.navigateToGitHubLogin() }
    }

    fun onCodeRetrieve(code: String) {
        Reporter.appAction(logTag, "onCodeRetrieve")
        getAccessToken(code)
    }

    fun onCodeErrorRetrieve(error: String) {
        Reporter.appAction(logTag, "onCodeErrorRetrieve")

        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = error)
    }

    fun checkCodeOnResume(){
        mCommands.enqueue { it.checkIsCodeExist() }
    }

    private fun getAccessToken(code: String) {
        Reporter.appAction(logTag, "getAccessToken")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)

        val disposable: Disposable = mNetworkRepository.getAccessToken(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    mState.value = oldState.copy(token = result.accessToken)
                },
                { error ->
                    mState.value = oldState.copy(errorMessage = error.message.toString())
                }
            )
        mCompositeDisposable.add(disposable)
    }

    fun saveUserToken() {
        Reporter.appAction(logTag, "saveUserToken")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)
        val disposable: Disposable = mDatabaseRepository.insertUser(User(token = "111"))
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mState.value = oldState.copy(isProgress = false)
                    mCommands.enqueue { it.navigateToMainMenu() }
                },
                { error ->
                    mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                })
        mCompositeDisposable.add(disposable)
    }

}
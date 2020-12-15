package com.klymchuk.githubrepos.ui.main.history

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.data.repositories.NetworkRepository
import com.klymchuk.githubrepos.navigation.Router
import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.main.history.list.HistoryItem
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val mRouter: Router,
    private val mDatabaseRepository: DatabaseRepository,
    private val mNetworkRepository: NetworkRepository,
    private val mMainCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    private val logTag = logTag()

    data class State(
        val isProgress: Boolean = false,
        val historyList: List<HistoryItem>,
        val errorMessage: String = "",
    ) {
        fun hasReposItems(): Boolean {
            return historyList.isNotEmpty()
        }
    }

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    init {
        mState = MutableLiveData(State(historyList = listOf()))
        mMainCommands.enqueue { it.showBackButton() }
    }

    fun getHistory(){
        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)
        val disposable: Disposable = mDatabaseRepository.getHistory()
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mState.value = oldState.copy(historyList = historyToHistoryItem(it))
                },
                { error ->
                    mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                })
        mCompositeDisposable.add(disposable)
    }

    fun onListItemClick(item: HistoryItem) {
        mMainCommands.enqueue { it.openLinkInBrowser(Uri.parse(item.htmlUrl)) }
    }

    fun errorMessageShown() {
        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = "")
    }

    fun onToolbarBackButtonClicked(){
        mMainCommands.enqueue { it.hideBackButton() }
        mRouter.back()
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    private fun historyToHistoryItem(historyList: List<History>): List<HistoryItem> {
        val list: MutableList<HistoryItem> = mutableListOf()

        historyList.forEach {
            list.add(
                HistoryItem(
                    id = it.id,
                    htmlUrl = it.htmlUrl,
                    fullName = it.fullName,
                    description = it.description,
                    stargazersCount = it.stargazersCount,
                )
            )
        }
        return list
    }

}
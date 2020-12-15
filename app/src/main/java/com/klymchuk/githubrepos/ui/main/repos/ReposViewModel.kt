package com.klymchuk.githubrepos.ui.main.repos

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.network.model.repos.ReposItem
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.data.repositories.NetworkRepository
import com.klymchuk.githubrepos.navigation.Destinations
import com.klymchuk.githubrepos.navigation.Router
import com.klymchuk.githubrepos.navigation.modifierFade
import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListItem
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val mRouter: Router,
    private val mDatabaseRepository: DatabaseRepository,
    private val mNetworkRepository: NetworkRepository,
    private val mMainCommands: MainViewCommandProcessor,
) : BaseViewModel() {

    private val logTag = logTag()

    data class State(
        val isProgress: Boolean = false,
        val reposList: List<ReposListItem>,
        val errorMessage: String = "",
        val searchText: String = "",
    ) {
        fun hasReposItems(): Boolean {
            return reposList.isNotEmpty()
        }
    }

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    init {
        mState = MutableLiveData(State(reposList = listOf()))
    }

    private fun getObservable(queryString: String): Observable<List<ReposItem>> {
        return Observable.zip(
            mNetworkRepository.getSearchReposResult(1, queryString),
            mNetworkRepository.getSearchReposResult(2, queryString),
            { t, u ->
                val reposItems = mutableListOf<ReposItem>()
                reposItems.addAll(t.reposItems)
                reposItems.addAll(u.reposItems)
                reposItems
            })
    }

    fun getSearchRepos(queryString: String) {
        Reporter.appAction(logTag, "getSearchRepos")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)

        val disposable: Disposable = getObservable(queryString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    mState.value = oldState.copy(reposList = reposToRecycleItem(result))
                },
                { error ->
                    mState.value = oldState.copy(errorMessage = error.message.toString())
                }
            )
        mCompositeDisposable.add(disposable)
    }

    fun saveHistoryItem(item: ReposListItem) {
        Reporter.appAction(logTag, "saveHistoryItem")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)
        val disposable: Disposable = mDatabaseRepository.insertHistoryItem(
            History(
                id = item.id,
                htmlUrl = item.htmlUrl,
                fullName = item.fullName,
                description = item.description,
                stargazersCount = item.stargazersCount,
                inputTimeStamp = (System.currentTimeMillis() / 1000).toString()
            )
        )
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mState.value = oldState.copy(isProgress = false)
                },
                { error ->
                    mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                })
        mCompositeDisposable.add(disposable)
    }

    fun onListItemClick(item: ReposListItem) {
        saveHistoryItem(item)
        mMainCommands.enqueue { it.openLinkInBrowser(Uri.parse(item.htmlUrl)) }
    }

    fun errorMessageShown() {
        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = "")
    }

    fun onEmptySearchText() {
        val oldState = mState.value!!
        mState.value = oldState.copy(reposList = listOf())
    }

    fun onHistoryButtonClicked() {
        mRouter.add(Destinations.history(), modifierFade())
    }

    fun onSearchTextChanged(text: String) {
        val oldState = mState.value!!

        if (oldState.searchText == text) return
        mState.value = oldState.copy(searchText = text)

        if (text.isNotBlank())
            getSearchRepos(text)
        else
            onEmptySearchText()
    }
    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    private fun reposToRecycleItem(reposItemList: List<ReposItem>): List<ReposListItem> {
        val list: MutableList<ReposListItem> = mutableListOf()

        reposItemList.forEach {
            list.add(
                ReposListItem(
                    id = it.id,
                    htmlUrl = it.htmlUrl,
                    fullName = it.fullName,
                    description = it.description.orEmpty(),
                    stargazersCount = it.stargazersCount,
                )
            )
        }
        return list
    }
}
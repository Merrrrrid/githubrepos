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
import com.klymchuk.githubrepos.utils.network.NetworkStatus
import com.klymchuk.githubrepos.utils.tokenFormatter
import io.reactivex.Single
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
        val reposList: MutableList<ReposListItem>,
        val errorMessage: String = "",
        val searchText: String = "",
        val incompleteResults: Boolean = true,
        val isLoadListEmpty: Boolean = false,
        val historyList: MutableList<Int>,
        val token: String = "",
    ) {
        fun hasReposItems(): Boolean {
            return reposList.isNotEmpty()
        }
    }

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    init {
        mState = MutableLiveData(State(reposList = mutableListOf(), historyList = mutableListOf()))
        getUserPrefs()
    }

    data class UserPrefs(
        val token: String,
        val historyList: List<Int>,
    )

    private fun getUserPrefZipSingle(): Single<UserPrefs> {
        Reporter.appAction(logTag, "getUserPrefZip")

        return Single.zip(
            mDatabaseRepository.getHistoryIds(),
            mDatabaseRepository.getUserToken(),
            { t, u ->
                val userPrefs = UserPrefs(u, t)
                userPrefs
            })
    }

    private fun getUserPrefs() {
        Reporter.appAction(logTag, "getUserPrefs")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)
        val disposable: Disposable = getUserPrefZipSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mState.value = oldState.copy(isProgress = false, token = it.token.tokenFormatter(), historyList = it.historyList.toMutableList())
                },
                { error ->
                    mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                })
        mCompositeDisposable.add(disposable)
    }

    private fun getSingleZipQueries(queryString: String): Single<List<ReposItem>> {
        Reporter.appAction(logTag, "getSingleZipQueries")

        val oldState = mState.value!!
        return Single.zip(
            mNetworkRepository.getSearchReposResult(oldState.token, queryString),
            mNetworkRepository.getSearchReposResult(oldState.token, queryString),
            { t, u ->
                val reposItems = mutableListOf<ReposItem>()
                reposItems.addAll(t.reposItems)
                reposItems.addAll(u.reposItems)
                reposItems
            })
    }

    private fun getSearchRepos(queryString: String) {
        Reporter.appAction(logTag, "getSearchRepos")

        if (NetworkStatus.isNetworkConnected) {
            val oldState = mState.value!!
            mState.value = oldState.copy(isProgress = true)

            mNetworkRepository.initPageToDefValue()

            val disposable: Disposable = getSingleZipQueries(queryString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        mState.value = oldState.copy(isProgress = false, reposList = reposToRecycleItem(oldState, result), isLoadListEmpty = result.isEmpty())
                    },
                    { error ->
                        mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                    }
                )
            mCompositeDisposable.add(disposable)
        } else {
            val oldState = mState.value!!
            mState.value = oldState.copy(errorMessage = "Check your Internet connection")
        }
    }

    private fun loadNextPage() {
        Reporter.appAction(logTag, "loadNextPage")
        if (NetworkStatus.isNetworkConnected) {
            val oldState = mState.value!!
            mState.value = oldState.copy(isProgress = true)

            val disposable: Disposable = getSingleZipQueries(oldState.searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        val newList = oldState.reposList.toMutableList()
                        newList.addAll(reposToRecycleItem(oldState, result))
                        mState.value = oldState.copy(isProgress = false, reposList = newList, isLoadListEmpty = result.isEmpty())
                    },
                    { error ->
                        mState.value = oldState.copy(isProgress = false, errorMessage = error.message.toString())
                    }
                )
            mCompositeDisposable.add(disposable)
        } else {
            val oldState = mState.value!!
            mState.value = oldState.copy(errorMessage = "Check your Internet connection")
        }
    }

    fun onScrolledToEnd() {
        Reporter.appAction(logTag, "onScrolledToEnd")

        val oldState = mState.value!!
        if (!oldState.isProgress && !oldState.isLoadListEmpty) {
            loadNextPage()
        }
    }

    private fun saveHistoryItem(item: ReposListItem) {
        Reporter.appAction(logTag, "saveHistoryItem")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)

        if (!oldState.historyList.contains(item.id)) {
            oldState.historyList.add(item.id)
            val oldItem = oldState.reposList.indexOf(oldState.reposList.find { it.id == item.id })
            oldState.reposList[oldItem] = oldState.reposList.find { it.id == item.id }!!.copy(isSeen = true)
            mState.value = oldState.copy(historyList = oldState.historyList, reposList = oldState.reposList)
        }

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
        Reporter.userAction(logTag, "onListItemClick")

        mMainCommands.enqueue { it.openLinkInBrowser(Uri.parse(item.htmlUrl)) }
        saveHistoryItem(item)
    }

    fun errorMessageShown() {
        Reporter.appAction(logTag, "errorMessageShown")

        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = "")
    }

    private fun onEmptySearchText() {
        Reporter.appAction(logTag, "onEmptySearchText")

        val oldState = mState.value!!
        mState.value = oldState.copy(reposList = mutableListOf())
    }

    fun onHistoryButtonClicked() {
        Reporter.userAction(logTag, "onHistoryButtonClicked")

        mRouter.add(Destinations.history(), modifierFade())
        mMainCommands.enqueue { it.hideKeyboard() }
    }

    fun onSearchTextChanged(text: String) {
        Reporter.appAction(logTag, "onSearchTextChanged")

        val oldState = mState.value!!

        if (oldState.searchText == text) return
        // Related to small api limit and the impossibility of making a request for every text change
        if (oldState.isProgress) return

        mState.value = oldState.copy(searchText = text)

        if (text.isNotBlank())
            getSearchRepos(text)
        else
            onEmptySearchText()
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    private fun reposToRecycleItem(state: State, reposItemList: List<ReposItem>): MutableList<ReposListItem> {
        val list: MutableList<ReposListItem> = mutableListOf()

        reposItemList.forEach {
            list.add(
                ReposListItem(
                    id = it.id,
                    htmlUrl = it.htmlUrl,
                    fullName = it.fullName,
                    description = it.description.orEmpty(),
                    stargazersCount = it.stargazersCount,
                    isSeen = state.historyList.contains(it.id)
                )
            )
        }
        return list
    }

}
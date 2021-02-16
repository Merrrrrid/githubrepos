package com.klymchuk.githubrepos.ui.main.repos

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.network.model.repos.ReposItem
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.data.repositories.NetworkRepository
import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.ViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListItem
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import com.klymchuk.githubrepos.utils.network.NetworkStatus
import com.klymchuk.githubrepos.utils.tokenFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReposViewModel @ViewModelInject constructor(
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

    private val mCommands: ViewCommandProcessor<ReposFragment> = ViewCommandProcessor()
    fun observeCommands(owner: LifecycleOwner, view: ReposFragment) {
        mCommands.observe(owner, view)
    }

    private val mState: MutableLiveData<State>
    fun state(): LiveData<State> = mState

    init {
        mState = MutableLiveData(State(reposList = mutableListOf(), historyList = mutableListOf()))
        getUserPrefs()
    }

    private fun getUserPrefs() {
        Reporter.appAction(logTag, "getUserPrefs")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)


        viewModelScope.launch(Dispatchers.IO) {

            val historyIds: List<Int> = mDatabaseRepository.getHistoryIds()
            val userToken: String? = mDatabaseRepository.getUserToken()

            withContext(Dispatchers.Main) {
                mState.value = oldState.copy(isProgress = false, token = userToken.orEmpty().tokenFormatter(), historyList = historyIds.toMutableList())
            }
        }

    }

    private fun getSearchRepos(queryString: String) {
        Reporter.appAction(logTag, "getSearchRepos")

        if (NetworkStatus.isNetworkConnected) {
            val oldState = mState.value!!
            mState.value = oldState.copy(isProgress = true)

            mNetworkRepository.initPageToDefValue()

            viewModelScope.launch(Dispatchers.IO){
                val result = mNetworkRepository.getSearchReposResult(oldState.token, queryString)
                withContext(Dispatchers.Main){
                    mState.value = oldState.copy(isProgress = false, reposList = reposToRecycleItem(oldState, result.reposItems), isLoadListEmpty = result.reposItems.isEmpty())
                }
            }

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

            viewModelScope.launch(Dispatchers.IO){
                val result = mNetworkRepository.getSearchReposResult(oldState.token, oldState.searchText)
                withContext(Dispatchers.Main){
                    val newList = oldState.reposList.toMutableList()
                    newList.addAll(reposToRecycleItem(oldState, result.reposItems))
                    mState.value = oldState.copy(isProgress = false, reposList = newList, isLoadListEmpty = result.reposItems.isEmpty())
                }
            }

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

        viewModelScope.launch(Dispatchers.IO) {

            mDatabaseRepository.insertHistoryItem(
                History(
                    id = item.id,
                    htmlUrl = item.htmlUrl,
                    fullName = item.fullName,
                    description = item.description,
                    stargazersCount = item.stargazersCount,
                    inputTimeStamp = (System.currentTimeMillis() / 1000).toString()
                )
            )

            withContext(Dispatchers.Main) {
                mState.value = oldState.copy(isProgress = false)
            }
        }

    }

    fun onHistoryButtonClicked() {
        Reporter.userAction(logTag, "onHistoryButtonClicked")

        mCommands.enqueue { it.navigateToHistory() }
    }

    fun onListItemClick(item: ReposListItem) {
        Reporter.userAction(logTag, "onListItemClick")

        mMainCommands.enqueue { it.navigateToRepoDetails(item.htmlUrl, item.fullName) }
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

    fun onSearchTextChanged(text: String) {
        Reporter.appAction(logTag, "onSearchTextChanged")

        val oldState = mState.value!!

        if (oldState.searchText == text) return
        if (oldState.isProgress) return

        mState.value = oldState.copy(searchText = text)

        if (text.isNotBlank())
            getSearchRepos(text)
        else
            onEmptySearchText()
    }

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
                    isSeen = false//state.historyList.contains(it.id)
                )
            )
        }
        return list
    }

}
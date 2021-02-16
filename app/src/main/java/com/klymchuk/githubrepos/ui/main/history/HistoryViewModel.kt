package com.klymchuk.githubrepos.ui.main.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.klymchuk.githubrepos.data.db.entity.History
import com.klymchuk.githubrepos.data.repositories.DatabaseRepository
import com.klymchuk.githubrepos.ui.MainViewCommandProcessor
import com.klymchuk.githubrepos.ui.base.commands.enqueue
import com.klymchuk.githubrepos.ui.base.fragment.BaseViewModel
import com.klymchuk.githubrepos.ui.main.history.list.HistoryItem
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel @ViewModelInject constructor(
    private val mDatabaseRepository: DatabaseRepository,
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
        getHistory()
    }

    private fun getHistory() {
        Reporter.appAction(logTag, "getHistory")

        val oldState = mState.value!!
        mState.value = oldState.copy(isProgress = true)

        viewModelScope.launch(Dispatchers.IO) {

            val history: List<History> = mDatabaseRepository.getHistory()

            withContext(Dispatchers.Main) {
                mState.value = oldState.copy(isProgress = false, historyList = historyToHistoryItem(history))
            }
        }

    }

    fun onListItemClick(item: HistoryItem) {
        Reporter.userAction(logTag, "onListItemClick")

        mMainCommands.enqueue { it.navigateToRepoDetails(item.htmlUrl, item.fullName) }
    }

    fun errorMessageShown() {
        Reporter.appAction(logTag, "errorMessageShown")

        val oldState = mState.value!!
        mState.value = oldState.copy(errorMessage = "")
    }

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
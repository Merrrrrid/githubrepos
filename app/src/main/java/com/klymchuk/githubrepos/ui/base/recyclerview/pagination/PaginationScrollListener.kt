package com.klymchuk.githubrepos.ui.base.recyclerview.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PaginationScrollListener(
    private val mContract: Contract,
) : RecyclerView.OnScrollListener() {

    interface Contract {
        fun onLoadMore()
        fun getLayoutManager(): LinearLayoutManager
    }

    data class State(
        val isEnabled: Boolean = false,
        val thresholdEnd: Int = 5,
        val total: Int = 0,
    )

    private var mState = State()

    fun updateState(state: State) {
//        Logger.d(MainViewModel.TAG_BALANCE, "updatePagination $state")
        mState = state
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!mState.isEnabled) return

        val lastVisibleItemPosition = mContract.getLayoutManager().findLastVisibleItemPosition()

        if (isHitThreshold(lastVisibleItemPosition)) {
            mState = mState.copy(isEnabled = false)

            mContract.onLoadMore()
        }
    }

    private fun isHitThreshold(lastVisibleItemPosition: Int): Boolean {
        return lastVisibleItemPosition + mState.thresholdEnd >= mState.total
    }
}
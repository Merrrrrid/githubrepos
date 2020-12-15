package com.klymchuk.githubrepos.ui.main.history.list

import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem
import com.klymchuk.githubrepos.ui.main.repos.list.ReposAdapter


data class HistoryItem(
    val id: Int,
    val htmlUrl: String,
    val fullName: String,
    val description: String,
    val stargazersCount: Int,
) : RecyclerViewItem {

    override fun getViewType() = ReposAdapter.ViewType.REPOS

    override fun isSame(another: RecyclerViewItem): Boolean {
        if (another !is HistoryItem) return false

        return id == another.id
    }

    override fun isSameContent(another: RecyclerViewItem): Boolean {
        if (another !is HistoryItem) return false

        return false
    }
}
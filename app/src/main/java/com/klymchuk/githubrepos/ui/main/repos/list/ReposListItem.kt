package com.klymchuk.githubrepos.ui.main.repos.list

import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem


data class ReposListItem(
    val id: Int,
    val htmlUrl: String,
    val fullName: String,
    val description: String,
    val stargazersCount: Int,
) : RecyclerViewItem {

    override fun getViewType() = ReposAdapter.ViewType.REPOS

    override fun isSame(another: RecyclerViewItem): Boolean {
        if (another !is ReposListItem) return false

        return id == another.id
    }

    override fun isSameContent(another: RecyclerViewItem): Boolean {
        if (another !is ReposListItem) return false

        return false
    }
}
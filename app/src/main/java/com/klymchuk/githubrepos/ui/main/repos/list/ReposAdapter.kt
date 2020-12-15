package com.klymchuk.githubrepos.ui.main.repos.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.ui.base.recyclerview.BaseRecyclerViewAdapter
import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem


class ReposAdapter(
    var contractRepos: ReposListViewHolder.Contract? = null,
) : BaseRecyclerViewAdapter<RecyclerViewItem>() {

    object ViewType {
        const val REPOS = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.REPOS -> ReposListViewHolder.newInstance(mInflater!!, parent, contractRepos)

            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.REPOS -> (holder as ReposListViewHolder).bind(getItem(position) as ReposListItem)
            else -> super.onBindViewHolder(holder, position)
        }
    }
}
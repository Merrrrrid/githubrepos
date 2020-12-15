package com.klymchuk.githubrepos.ui.main.history.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.ui.base.recyclerview.BaseRecyclerViewAdapter
import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem


class HistoryAdapter(
    var contractHistory: HistoryViewHolder.Contract? = null,
) : BaseRecyclerViewAdapter<RecyclerViewItem>() {

    object ViewType {
        const val HISTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HISTORY -> HistoryViewHolder.newInstance(mInflater!!, parent, contractHistory)

            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.HISTORY -> (holder as HistoryViewHolder).bind(getItem(position) as HistoryItem)
            else -> super.onBindViewHolder(holder, position)
        }
    }
}
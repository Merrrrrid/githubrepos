package com.klymchuk.githubrepos.ui.base.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem

class DiffCallback (
    private val oldList: List<RecyclerViewItem>,
    private val newList: List<RecyclerViewItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].isSame(newList[newPos])

    override fun areContentsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].isSameContent(newList[newPos])
}

fun <T : RecyclerViewItem> BaseRecyclerViewAdapter<T>.applyDiffUtil(newList: List<T>) {
    val diffResult = DiffUtil.calculateDiff(DiffCallback(list, newList))
    list = newList
    diffResult.dispatchUpdatesTo(this)
}
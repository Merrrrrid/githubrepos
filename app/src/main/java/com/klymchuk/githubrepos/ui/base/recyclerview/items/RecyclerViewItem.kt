package com.klymchuk.githubrepos.ui.base.recyclerview.items

interface RecyclerViewItem  {

    fun getViewType(): Int

    /**
     * @see androidx.recyclerview.widget.DiffUtil.Callback.areItemsTheSame
     */
    fun isSame(another: RecyclerViewItem): Boolean

    /**
     * @see androidx.recyclerview.widget.DiffUtil.Callback.areContentsTheSame
     */
    fun isSameContent(another: RecyclerViewItem): Boolean

}
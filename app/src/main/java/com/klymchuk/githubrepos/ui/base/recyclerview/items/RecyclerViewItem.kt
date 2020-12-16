package com.klymchuk.githubrepos.ui.base.recyclerview.items

interface RecyclerViewItem  {

    fun getViewType(): Int

    fun isSame(another: RecyclerViewItem): Boolean

    fun isSameContent(another: RecyclerViewItem): Boolean

}
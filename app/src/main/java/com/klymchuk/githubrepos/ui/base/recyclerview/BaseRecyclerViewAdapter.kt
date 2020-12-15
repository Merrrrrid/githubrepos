package com.klymchuk.githubrepos.ui.base.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.ui.base.recyclerview.items.RecyclerViewItem

abstract class BaseRecyclerViewAdapter<Item : RecyclerViewItem>(
    var list: List<Item> = emptyList(),
//    var contractError: ErrorViewHolder.Contract? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOADING = 101
        const val VIEW_TYPE_ERROR = 102
    }

    protected var mInflater: LayoutInflater? = null
    protected var mRecyclerView: RecyclerView? = null

    fun getItem(position: Int): Item = list[position]
    override fun getItemCount() = list.size
    override fun getItemViewType(position: Int) = list[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            VIEW_TYPE_LOADING -> LoadingViewHolder.newInstance(mInflater!!, parent)
//            VIEW_TYPE_ERROR -> ErrorViewHolder.newInstance(mInflater!!, parent, contractError)

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (item.getViewType()) {
//            VIEW_TYPE_LOADING -> (holder as LoadingViewHolder).bind(item as LoadingItem, position)
//            VIEW_TYPE_ERROR -> (holder as ErrorViewHolder).bind(item as ErrorItem, position)

            else -> throw IllegalArgumentException("Unknown ViewType: ${item.getViewType()}")
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mInflater = LayoutInflater.from(recyclerView.context)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = null
        mInflater = null
        super.onDetachedFromRecyclerView(recyclerView)
    }


    //==============================================================================================
    // *** VH Lifecycle ***
    //==============================================================================================
    interface AttachableToWindow {
        fun onAttachedToWindow()
        fun onDetachedFromWindow()
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder is AttachableToWindow) holder.onAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is AttachableToWindow) holder.onDetachedFromWindow()

        super.onViewDetachedFromWindow(holder)
    }
}
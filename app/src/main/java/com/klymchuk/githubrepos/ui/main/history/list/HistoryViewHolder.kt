package com.klymchuk.githubrepos.ui.main.history.list

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.databinding.LiReposBinding
import com.klymchuk.githubrepos.utils.Logger
import com.klymchuk.githubrepos.utils.logTag

class HistoryViewHolder(
    private val mBinding: LiReposBinding,
    private val mContract: Contract?,
) : RecyclerView.ViewHolder(mBinding.root) {

    private val logTag = logTag()

    init {
        Logger.d(logTag, "onCreate  ${hashCode()}")
    }

    interface Contract {
        fun onClickItem(item: HistoryItem)
    }

    companion object {
        fun newInstance(inflater: LayoutInflater, parent: ViewGroup, contract: Contract?): HistoryViewHolder {
            return HistoryViewHolder(LiReposBinding.inflate(inflater, parent, false), contract)
        }
    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    private var mLastItem: HistoryItem? = null

    fun bind(item: HistoryItem) {

        if (item !== mLastItem) {
            Logger.d(logTag, "bind ${item.id}")
            renderItem(item)
        } else {
            Logger.d(logTag, "NOT bind ${item.id}")
        }

        mLastItem = item
    }

    private fun renderItem(item: HistoryItem) {
        val context = mBinding.root.context
        mBinding.root.setOnClickListener {
            mContract?.onClickItem(item)
        }
        mBinding.title.text = item.fullName
        mBinding.description.text = item.description
        mBinding.stars.text = item.stargazersCount.toString()
    }

}
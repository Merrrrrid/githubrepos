package com.klymchuk.githubrepos.ui.main.repos.list

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.databinding.LiReposBinding
import com.klymchuk.githubrepos.utils.Logger
import com.klymchuk.githubrepos.utils.logTag

class ReposListViewHolder(
    private val mBinding: LiReposBinding,
    private val mContract: Contract?,
) : RecyclerView.ViewHolder(mBinding.root) {

    private val logTag = logTag()

    init {
        Logger.d(logTag, "onCreate  ${hashCode()}")
    }

    interface Contract {
        fun onClickItem(item: ReposListItem)
    }

    companion object {
        fun newInstance(inflater: LayoutInflater, parent: ViewGroup, contract: Contract?): ReposListViewHolder {
            return ReposListViewHolder(LiReposBinding.inflate(inflater, parent, false), contract)
        }
    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    private var mLastItem: ReposListItem? = null

    fun bind(item: ReposListItem) {

        if (item !== mLastItem) {
            Logger.d(logTag, "bind ${item.id}")
            renderItem(item)
        } else {
            Logger.d(logTag, "NOT bind ${item.id}")
        }

        mLastItem = item
    }

    private fun renderItem(item: ReposListItem) {
        val context = mBinding.root.context
        mBinding.root.setOnClickListener {
            mContract?.onClickItem(item)
        }
        mBinding.title.text = item.fullName
        mBinding.description.text = item.description
        mBinding.stars.text = item.stargazersCount.toString()
    }

}
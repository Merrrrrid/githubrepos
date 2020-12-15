package com.klymchuk.githubrepos.ui.main.history

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.HistoryFragmentBinding
import com.klymchuk.githubrepos.ui.MainActivity
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.base.fragment.newViewModelWithArgs
import com.klymchuk.githubrepos.ui.base.recyclerview.CustomLinearLayoutManager
import com.klymchuk.githubrepos.ui.base.recyclerview.applyDiffUtil
import com.klymchuk.githubrepos.ui.main.history.list.HistoryAdapter
import com.klymchuk.githubrepos.ui.main.history.list.HistoryItem
import com.klymchuk.githubrepos.ui.main.history.list.HistoryViewHolder
import com.klymchuk.githubrepos.utils.binding.viewBinding
import com.klymchuk.githubrepos.utils.disableItemChangeAnimation
import com.klymchuk.githubrepos.utils.gone
import com.klymchuk.githubrepos.utils.logTag
import com.klymchuk.githubrepos.utils.show


class HistoryFragment : BaseFragment(R.layout.history_fragment) {

    private val logTag = logTag()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = newViewModelWithArgs()
    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    private val mBinding: HistoryFragmentBinding by viewBinding(HistoryFragmentBinding::bind)
    private lateinit var mViewModel: HistoryViewModel
    private lateinit var mHistoryAdapter: HistoryAdapter

    override fun initUI() {
        mHistoryAdapter = HistoryAdapter(
            contractHistory = object : HistoryViewHolder.Contract {
                override fun onClickItem(item: HistoryItem) {
                    mViewModel.onListItemClick(item)
                }
            },
        )

        setHasOptionsMenu(true)

        mBinding.historyRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext())
        mBinding.historyRecyclerView.adapter = mHistoryAdapter
        mBinding.historyRecyclerView.disableItemChangeAnimation()

        observeState()

        mViewModel.getHistory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mViewModel.onToolbarBackButtonClicked()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: HistoryViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(viewLifecycleOwner) { state ->

            if (shouldUpdateHistory(state)) {
                mHistoryAdapter.applyDiffUtil(state.historyList)

                if (state.hasReposItems()) {
                    mBinding.emptyList.gone()
                } else {
                    mBinding.emptyList.show()
                }
            }
            if (shouldShowError(state))
                showErrorMessage(state.errorMessage)
            mLastConsumedState = state
        }
    }

    private fun shouldUpdateHistory(state: HistoryViewModel.State): Boolean {
        if (mLastConsumedState == null) return true

        return mLastConsumedState?.historyList != state.historyList
    }

    //todo change logic
    private fun shouldShowError(state: HistoryViewModel.State): Boolean {
        if (state.errorMessage.isBlank()) return false
        if (mLastConsumedState == null) return true

        return mLastConsumedState?.errorMessage != state.errorMessage
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        mViewModel.errorMessageShown()
    }

}
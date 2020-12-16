package com.klymchuk.githubrepos.ui.main.history

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.HistoryFragmentBinding
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
import com.klymchuk.githubrepos.utils.show


class HistoryFragment : BaseFragment(R.layout.history_fragment) {

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
        mViewModel.initBackButton()

        //RecyclerView
        mBinding.historyRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext())
        mBinding.historyRecyclerView.adapter = mHistoryAdapter
        mBinding.historyRecyclerView.disableItemChangeAnimation()
        mBinding.historyRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(HistoryAdapter.ViewType.HISTORY, 20)
        })
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

    override fun onDestroyView() {
        mViewModel.hideBackButton()
        mLastConsumedState = null
        super.onDestroyView()
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: HistoryViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(viewLifecycleOwner) { state ->

            if (shouldUpdateProgress(state)) {
                if (state.isProgress) {
                    mBinding.progressBar.show()
                } else {
                    mBinding.progressBar.gone()
                }

            }

            mHistoryAdapter.applyDiffUtil(state.historyList)

            if (state.hasReposItems()) {
                mBinding.emptyList.gone()
            } else {
                mBinding.emptyList.show()
            }
            if (shouldShowError(state))
                showErrorMessage(state.errorMessage)
            mLastConsumedState = state
        }
    }

    private fun shouldUpdateProgress(state: HistoryViewModel.State): Boolean {
        return state.isProgress != mLastConsumedState?.isProgress
    }

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
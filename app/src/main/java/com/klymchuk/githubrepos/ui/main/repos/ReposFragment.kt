package com.klymchuk.githubrepos.ui.main.repos

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.ReposFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.base.fragment.newViewModelWithArgs
import com.klymchuk.githubrepos.ui.base.recyclerview.CustomLinearLayoutManager
import com.klymchuk.githubrepos.ui.base.recyclerview.applyDiffUtil
import com.klymchuk.githubrepos.ui.main.repos.list.ReposAdapter
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListItem
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListViewHolder
import com.klymchuk.githubrepos.utils.*
import com.klymchuk.githubrepos.utils.binding.viewBinding


class ReposFragment : BaseFragment(R.layout.repos_fragment) {

    private val logTag = logTag()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = newViewModelWithArgs()


    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    private val mBinding: ReposFragmentBinding by viewBinding(ReposFragmentBinding::bind)
    private lateinit var mViewModel: ReposViewModel
    private lateinit var mReposAdapter: ReposAdapter

    override fun initUI() {
        mReposAdapter = ReposAdapter(
            contractRepos = object : ReposListViewHolder.Contract {
                override fun onClickItem(item: ReposListItem) {
                    mViewModel.onListItemClick(item)
                }
            },
        )

        setHasOptionsMenu(true)

        mBinding.repoListRecyclerView.layoutManager = CustomLinearLayoutManager(requireContext())
        mBinding.repoListRecyclerView.adapter = mReposAdapter
        mBinding.repoListRecyclerView.disableItemChangeAnimation()

        mBinding.searchEditText.apply {
            addTextChangedListener(doOnTextChanged { text, _, _, _ ->
                mViewModel.onSearchTextChanged(text.toString())
            })
        }

        observeState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        val cancelTV = TextView(activity)
        cancelTV.text = "History"
        cancelTV.setTextColor(requireContext().getColor(R.color.white))
        cancelTV.setOnClickListener{ mViewModel.onHistoryButtonClicked()}
        cancelTV.setPadding(0, 0, 16, 0)
        cancelTV.setTypeface(null, Typeface.BOLD)
        cancelTV.textSize = 14f
        menu.add(0, 1, 1, "test").setActionView(cancelTV).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: ReposViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(viewLifecycleOwner) { state ->

            mBinding.searchEditText.setTextIfDistinct(state.searchText)

            if (shouldUpdateHistory(state)) {
                mReposAdapter.applyDiffUtil(state.reposList)

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

    private fun shouldUpdateHistory(state: ReposViewModel.State): Boolean {
        if (mLastConsumedState == null) return true

        return mLastConsumedState?.reposList != state.reposList
    }


    //todo change logic
    private fun shouldShowError(state: ReposViewModel.State): Boolean {
        if (state.errorMessage.isBlank()) return false
        if (mLastConsumedState == null) return true

        return mLastConsumedState?.errorMessage != state.errorMessage
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        mViewModel.errorMessageShown()
    }

}
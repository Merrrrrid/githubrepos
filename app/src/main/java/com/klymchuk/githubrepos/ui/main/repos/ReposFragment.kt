package com.klymchuk.githubrepos.ui.main.repos

import android.content.Context
import android.graphics.Typeface
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = newViewModelWithArgs()
    }

    override fun onDestroyView() {
        mLastConsumedState = null
        super.onDestroyView()
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
                    if (mLastConsumedState != null && !mLastConsumedState!!.isProgress)
                        mViewModel.onListItemClick(item)
                }
            },
        )

        setHasOptionsMenu(true)

        //RecyclerView
        val layoutManager = CustomLinearLayoutManager(requireContext())
        mBinding.repoListRecyclerView.layoutManager = layoutManager
        mBinding.repoListRecyclerView.adapter = mReposAdapter
        mBinding.repoListRecyclerView.disableItemChangeAnimation()
        mBinding.repoListRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(ReposAdapter.ViewType.REPOS, 20)
        })
        mBinding.repoListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                    mViewModel.onScrolledToEnd()
                }
            }
        })

        mBinding.searchEditText.apply {
            addTextChangedListener(doOnTextChanged { text, _, _, _ ->
                mViewModel.onSearchTextChanged(text.toString())
            })
        }

        observeState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        menu.add(0, 1, 1, "repos").setActionView(menuTextButtonInit()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: ReposViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(viewLifecycleOwner) { state ->

            if (shouldUpdateProgress(state)) {
                if (state.isProgress) {
                    mBinding.progressBar.show()
                } else {
                    mBinding.progressBar.gone()
                }

            }

            // Related to small api limit and the impossibility of making a request for every text change
            mViewModel.onSearchTextChanged(mBinding.searchEditText.text.toString())

            mReposAdapter.applyDiffUtil(state.reposList)

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

    private fun shouldUpdateProgress(state: ReposViewModel.State): Boolean {
        return state.isProgress != mLastConsumedState?.isProgress
    }

    private fun shouldShowError(state: ReposViewModel.State): Boolean {
        if (state.errorMessage.isBlank()) return false
        if (mLastConsumedState == null) return true

        return mLastConsumedState?.errorMessage != state.errorMessage
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        mViewModel.errorMessageShown()
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    private fun menuTextButtonInit(): TextView {
        return TextView(requireActivity()).apply {
            text = requireContext().getString(R.string.history)
            setTextColor(requireContext().getColor(R.color.white))
            setOnClickListener { mViewModel.onHistoryButtonClicked() }
            setPadding(0, 0, 16, 0)
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
        }

    }
}
package com.klymchuk.githubrepos.ui.main.repos

import android.graphics.Typeface
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.ReposFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.base.recyclerview.CustomLinearLayoutManager
import com.klymchuk.githubrepos.ui.base.recyclerview.applyDiffUtil
import com.klymchuk.githubrepos.ui.main.repos.list.ReposAdapter
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListItem
import com.klymchuk.githubrepos.ui.main.repos.list.ReposListViewHolder
import com.klymchuk.githubrepos.utils.DebouncingQueryTextListener
import com.klymchuk.githubrepos.utils.binding.viewBinding
import com.klymchuk.githubrepos.utils.disableItemChangeAnimation
import com.klymchuk.githubrepos.utils.gone
import com.klymchuk.githubrepos.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReposFragment : BaseFragment(R.layout.repos_fragment) {

    override fun onDestroyView() {
        mLastConsumedState = null
        super.onDestroyView()
    }

    private val mBinding: ReposFragmentBinding by viewBinding(ReposFragmentBinding::bind)
    private  val mViewModel: ReposViewModel by viewModels()
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

        mBinding.searchView.setOnQueryTextListener(
            DebouncingQueryTextListener(
                lifecycle
            ) { newText ->
                newText?.let {
                    mViewModel.onSearchTextChanged(it)
                }
            }
        )

        mViewModel.observeCommands(viewLifecycleOwner, this)
        observeState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        menu.add(0, 1, 1, "repos").setActionView(menuTextButtonInit()).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

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

    fun navigateToHistory() {
        val action = ReposFragmentDirections.actionReposFragmentToHistoryFragment()
        findNavController().navigate(action)
    }

}
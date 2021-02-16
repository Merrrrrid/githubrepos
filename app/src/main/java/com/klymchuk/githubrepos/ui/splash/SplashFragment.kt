package com.klymchuk.githubrepos.ui.splash

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.logTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.splash_fragment) {

    private val logTag = logTag()

    private val mViewModel: SplashViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

     override fun initUI() {
        mViewModel.checkUserInDBOnStart()
        observeState()
    }

    private fun observeState() {
        mViewModel.state().observe(viewLifecycleOwner) { state ->
            if (state.userFromDB != null)
                if (state.userFromDB.token.isNotEmpty()) {
                    navigateToRepos()
                } else {
                    navigateToLogin()
                }
        }
    }

    private fun navigateToRepos() {
        Reporter.appAction(logTag, "navigateToRepos")

        val action = SplashFragmentDirections.actionSplashFragmentToReposFragment()
        findNavController().navigate(action)
    }

    private fun navigateToLogin() {
        Reporter.appAction(logTag, "navigateToLogin")

        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        findNavController().navigate(action)
    }

}
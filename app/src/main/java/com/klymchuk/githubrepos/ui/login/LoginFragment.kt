package com.klymchuk.githubrepos.ui.login

import androidx.fragment.app.viewModels
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.LoginFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.utils.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.login_fragment) {

    private val mBinding: LoginFragmentBinding by viewBinding(LoginFragmentBinding::bind)
    private val mViewModel: LoginViewModel by viewModels()

    override fun initUI() {
        mBinding.loginButton.setOnClickListener {
            mViewModel.onLoginButtonClicked()
        }
    }

}
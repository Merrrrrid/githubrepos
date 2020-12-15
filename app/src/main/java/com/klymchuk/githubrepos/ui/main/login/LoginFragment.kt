package com.klymchuk.githubrepos.ui.main.login

import android.content.Context
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.LoginFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.base.fragment.newViewModelWithArgs
import com.klymchuk.githubrepos.utils.binding.viewBinding
import com.klymchuk.githubrepos.utils.logTag


class LoginFragment : BaseFragment(R.layout.login_fragment) {

    private val logTag = logTag()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel = newViewModelWithArgs()
    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    private val mBinding: LoginFragmentBinding by viewBinding(LoginFragmentBinding::bind)
    private lateinit var mViewModel: LoginViewModel

    override fun initUI() {
        mBinding.loginLabel.setOnClickListener {
        }
    }

}
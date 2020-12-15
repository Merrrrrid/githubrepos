package com.klymchuk.githubrepos.ui.main.repos

import android.content.Context
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.ReposFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.ui.base.fragment.newViewModelWithArgs
import com.klymchuk.githubrepos.utils.binding.viewBinding
import com.klymchuk.githubrepos.utils.logTag


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

    override fun initUI() {



        mBinding.hi.setOnClickListener {

        }
    }

}
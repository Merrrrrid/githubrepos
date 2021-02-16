package com.klymchuk.githubrepos.ui.main.repodetails

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.RepoDetailsFragmentBinding
import com.klymchuk.githubrepos.ui.base.fragment.BaseFragment
import com.klymchuk.githubrepos.utils.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepoDetailsFragment : BaseFragment(R.layout.repo_details_fragment) {

    private val args: RepoDetailsFragmentArgs by navArgs()

    private val mViewModel: RepoDetailsViewModel by viewModels()

    private val mBinding: RepoDetailsFragmentBinding by viewBinding(RepoDetailsFragmentBinding::bind)

    override fun initUI() {
        val url = args.url
        val webSettings = mBinding.webView.settings
        webSettings.javaScriptEnabled = true
        mBinding.webView.loadUrl(url)
        mBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
    }

}